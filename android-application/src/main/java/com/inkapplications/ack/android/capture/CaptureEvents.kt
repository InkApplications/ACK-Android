package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.SettingsWriteAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.settings.setData
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.data.drivers.PacketDriver
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.EncodingPreference
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.PacketRoute
import com.inkapplications.android.extensions.location.LocationAccess
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.combineTriple
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

/**
 * Provides data access to abstractions around capturing APRS packets.
 */
@Singleton
class CaptureEvents @Inject constructor(
    private val drivers: PacketDrivers,
    private val readSettings: SettingsReadAccess,
    private val writeSettings: SettingsWriteAccess,
    private val connectionSettings: ConnectionSettings,
    private val transmitSettings: TransmitSettings,
    private val locationAccess: LocationAccess,
    private val logger: KimchiLogger,
) {
    private val mutableConnectionState = MutableStateFlow(ConnectionState.Disconnected)
    private val connectionEnabled = mutableConnectionState.map { it != ConnectionState.Disconnected }.distinctUntilChanged()
    private val mutableLocationTransmitState = MutableStateFlow(false)

    private val currentDriver = readSettings.observeData(connectionSettings.driver)
        .map {
            when (it) {
                DriverSelection.Audio -> drivers.afskDriver
                DriverSelection.Internet -> drivers.internetDriver
                DriverSelection.Tnc -> drivers.tncDriver
            }
        }

    /**
     * The current state of the selected APRS driver connection.
     */
    val connectionState: StateFlow<ConnectionState> = mutableConnectionState

    /**
     * Whether the option to repeatedly transmit the device location is enabled.
     */
    val locationTransmitState: StateFlow<Boolean> = mutableLocationTransmitState

    /**
     * The current audio input level, or null when not capturing.
     */
    val audioInputVolume = drivers.afskDriver.volume

    /**
     * Change the current APRS driver.
     *
     * Note: This will disconnect the current driver and location state
     * if enabled, and will require a reconnection.
     */
    fun changeDriver(value: DriverSelection) {
        mutableConnectionState.value = ConnectionState.Disconnected
        mutableLocationTransmitState.value = false
        writeSettings.setData(connectionSettings.driver, value)
    }

    /**
     * Connect or disconnect the currently selected APRS driver.
     *
     * This will transition through the [ConnectionState.Connecting] state
     * until the driver is ready during connection.
     */
    fun toggleConnectionState() {
        mutableConnectionState.updateAndGet {
            when (it) {
                ConnectionState.Disconnected -> ConnectionState.Connecting
                ConnectionState.Connecting, ConnectionState.Connected -> ConnectionState.Disconnected
            }
        }.also {
            if (it == ConnectionState.Disconnected) {
                mutableLocationTransmitState.value = false
            }
        }
    }

    /**
     * Enable/disable the repeated transmission of device location.
     */
    fun toggleLocationTransmitState() {
        mutableLocationTransmitState.getAndUpdate { !it }
    }

    /**
     * Bind listeners required to start the collection of APRS packets.
     *
     * This should be called from an activity to handle the start of the
     * [BackgroundCaptureService] as well as handling permissions requests
     * that drivers may need.
     */
    suspend fun collectConnectionEvents(
        requestPermissions: suspend (permissions: Set<String>) -> Boolean,
        startBackgroundService: suspend () -> Unit,
        stopBackgroundService: suspend () -> Unit,
    ) {
        connectionEnabled.collectLatest { connect ->
            if (connect) {
                currentDriver
                    .onEach { logger.debug { "Current Driver: ${it::class.simpleName}" } }
                    .collectLatest { driver ->
                        if (requestPermissions(driver.receivePermissions)) {
                            startBackgroundService()
                        } else {
                            logger.info("Permissions not granted. Stopping Service")
                            stopBackgroundService()
                        }
                    }
            } else {
                logger.info("Disconnecting Background Service.")
                stopBackgroundService()
            }
        }
    }

    /**
     * Start the collection and transmission of APRS packets for the current
     * driver.
     *
     * This should be called from a background service so that the application
     * does not need to remain in the foreground.
     *
     * This method will suspend indefinitely while connected! to stop
     * transmission, cancel the job that this is called inside.
     * Cancellation is handled gracefully and will set the connection state
     * back to [ConnectionState.Disconnected] when canceled.
     *
     * Note that the driver may become disconnected for external reasons,
     * such as when changing the source driver, which will also cause the
     * suspension to end.
     */
    suspend fun connectDriver() {
        logger.info("Connect Driver")
        currentDriver.combinePair(connectionState.filter { it != ConnectionState.Connected })
            .collectLatest { (driver, connection) ->
                if (connection != ConnectionState.Connecting) return@collectLatest
                logger.debug { "Connecting to Driver: ${driver::class.simpleName}" }
                coroutineScope {
                    mutableConnectionState.value = ConnectionState.Connected
                    val transmitJob = launch { transmitLoop(driver) }
                    launch {
                        try {
                            driver.connect()
                        } catch (e: Throwable) {
                            logger.error(e) { "Driver connection failed" }
                        } finally {
                            logger.info("Driver connection canceling")
                            transmitJob.cancel()
                            mutableConnectionState.value = ConnectionState.Disconnected
                            mutableLocationTransmitState.value = false
                        }
                    }
                }
            }
    }

    /**
     * Transmit an APRS packet to the specified driver at the configured interval.
     */
    private suspend fun transmitLoop(driver: PacketDriver) {
        readSettings.observeData(connectionSettings.address)
            .filterNotNull()
            .combine(readSettings.observeData(transmitSettings.digipath)) { callsign, path ->
                TransmitPrototype(
                    path = path,
                    destination = transmitSettings.destination.defaultData,
                    callsign = callsign,
                    symbol = transmitSettings.symbol.defaultData,
                    comment = transmitSettings.comment.defaultValue,
                    minRate = transmitSettings.minRate.defaultData,
                    maxRate = transmitSettings.maxRate.defaultData,
                    distance = transmitSettings.distance.defaultData,
                )
            }
            .combine(readSettings.observeData(transmitSettings.symbol)) { prototype, symbol ->
                prototype.copy(symbol = symbol)
            }
            .combine(readSettings.observeString(transmitSettings.comment)) { prototype, comment ->
                prototype.copy(comment = comment)
            }
            .combine(readSettings.observeData(transmitSettings.destination)) { prototype, destination ->
                prototype.copy(destination = destination)
            }
            .combine(readSettings.observeData(transmitSettings.minRate)) { prototype, rate ->
                prototype.copy(minRate = rate)
            }
            .combine(readSettings.observeData(transmitSettings.maxRate)) { prototype, rate ->
                prototype.copy(maxRate = rate)
            }
            .combine(readSettings.observeData(transmitSettings.distance)) { prototype, distance ->
                prototype.copy(distance = distance)
            }
            .flatMapLatest { prototype ->
                locationAccess.observeLocationChanges(prototype.maxRate, prototype.distance)
                    .map { prototype to it }
            }
            .combineTriple(locationTransmitState)
            .collectLatest { (prototype, update, transmitting) ->
                while (coroutineContext.isActive && transmitting) {
                    val packet = AprsPacket(
                        route = PacketRoute(
                            source = prototype.callsign,
                            digipeaters = prototype.path,
                            destination = prototype.destination,
                        ),
                        data = PacketData.Position(
                            coordinates = update.location,
                            symbol = prototype.symbol,
                            altitude = update.altitude,
                            comment = prototype.comment,
                        )
                    )
                    val encodingConfig = EncodingConfig(compression = EncodingPreference.Disfavored)

                    driver.transmitPacket(packet, encodingConfig)
                    delay(prototype.minRate)
                }
            }
    }
}
