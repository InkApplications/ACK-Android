package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.SettingsWriteAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.settings.setData
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.ack.data.drivers.PacketDriver
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.EncodingPreference
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.PacketRoute
import com.inkapplications.android.extensions.location.LocationAccess
import com.inkapplications.coroutines.combinePair
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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
    val driverSelection = readSettings.observeData(connectionSettings.driver)

    private val currentDriver = driverSelection
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
    val connectionState: Flow<DriverConnectionState> = currentDriver
        .flatMapLatest { it.connectionState }

    /**
     * Whether the option to repeatedly transmit the device location is enabled.
     */
    val locationTransmitState = MutableStateFlow(false)

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
    suspend fun changeDriver(value: DriverSelection) {
        currentDriver.first().disconnect()
        locationTransmitState.value = false
        writeSettings.setData(connectionSettings.driver, value)
    }

    suspend fun getDriverConnectPermissions(): Set<String> {
        return currentDriver.first().receivePermissions
    }

    suspend fun getDriverTransmitPermissions(): Set<String> {
        return currentDriver.first().transmitPermissions
    }

    suspend fun connectDriver() {
        currentDriver.first().connect()
    }

    suspend fun disconnectDriver() {
        currentDriver.first().disconnect()
    }

    suspend fun locationTransmitLoop() {
        locationTransmitState.combinePair(currentDriver)
            .collectLatest { (transmit, driver) ->
                if (transmit) transmitLoop(driver)
            }
    }

    /**
     * Transmit an APRS packet to the specified driver at the configured interval.
     */
    private suspend fun transmitLoop(driver: PacketDriver) {
        logger.trace("Starting transmit loop")
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
            .onEach { logger.debug("Location Transmit Prototype: $it") }
            .flatMapLatest { prototype ->
                locationAccess.observeLocationChanges(prototype.maxRate, prototype.distance)
                    .map { prototype to it }
                    .onCompletion { logger.warn("Location Transmit Stopped") }
            }
            .collectLatest { (prototype, update) ->
                while (coroutineContext.isActive) {
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
