package com.inkapplications.ack.android.capture

import android.Manifest
import com.inkapplications.android.extensions.location.LocationAccess
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.android.transmit.TransmitSettings
import com.inkapplications.ack.data.drivers.PacketDriver
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.ack.structures.*
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.combineTriple
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
    private val settings: SettingsReadAccess,
    private val connectionSettings: ConnectionSettings,
    private val transmitSettings: TransmitSettings,
    private val locationAccess: LocationAccess,
    private val logger: KimchiLogger,
) {
    private val mutableAudioListenState = MutableStateFlow(false)
    private val mutableInternetListenState = MutableStateFlow(false)
    private val mutableAudioTransmitState = MutableStateFlow(false)
    private val mutableInternetTransmitState = MutableStateFlow(false)

    /**
     * Whether the application is currently capturing audio packets
     */
    val audioListenState: StateFlow<Boolean> = mutableAudioListenState

    /**
     * Whether the application is currently capturing internet packets
     */
    val internetListenState: StateFlow<Boolean> = mutableInternetListenState

    /**
     * Whether the application is currently transmitting audio packets
     */
    val audioTransmitState: StateFlow<Boolean> = mutableAudioTransmitState

    /**
     * Whether the application is currently transmitting internet packets.
     */
    val internetTransmitState: StateFlow<Boolean> = mutableInternetTransmitState

    /**
     * The current audio input level, or null when not capturing.
     */
    val audioInputVolume = drivers.afskDriver.volume

    private val combinedTransmitState = mutableInternetTransmitState.combinePair(mutableAudioTransmitState)

    /**
     * Android permissions required for audio capture.
     */
    val audioCapturePermissions = drivers.afskDriver.receivePermissions

    /**
     * Android permissions required for audio transmit.
     */
    val audioTransmitPermissions = drivers.afskDriver.transmitPermissions + Manifest.permission.ACCESS_FINE_LOCATION

    /**
     * Android permissions required for internet capture.
     */
    val internetCapturePermissions = drivers.internetDriver.receivePermissions

    /**
     * Android permissions required for internet transmit.
     */
    val internetTransmitPermissions = drivers.internetDriver.transmitPermissions + Manifest.permission.ACCESS_FINE_LOCATION

    /**
     * Connect the audio driver for listening to audio packets.
     *
     * This must be running for audio transmit to work, in addition to capture.
     */
    suspend fun connectAudio() {
        coroutineScope {
            launch { listenForPackets() }
            launch {
                mutableAudioTransmitState.collectLatest {
                    if (it) transmitLoop(drivers.afskDriver)
                }
            }
        }
    }

    /**
     * Connect to the internet capture driver for capturing packets via APRS-IS
     *
     * This must be running for internet transmit to work in addition to
     * capture.
     */
    suspend fun connectInternet() {
        coroutineScope {
            launch { listenForInternetPackets() }
            launch {
                mutableInternetTransmitState.collectLatest {
                    if (it) transmitLoop(drivers.internetDriver)
                }
            }
        }
    }

    /**
     * Start transmitting audio packets.
     *
     * Note: [connectAudio] must be running for this to have an effect.
     */
    fun startAudioTransmit() {
        mutableAudioTransmitState.value = true
    }

    /**
     * Stop transmitting audio packets.
     */
    fun stopAudioTransmit() {
        mutableAudioTransmitState.value = false
    }

    /**
     * Start transmitting internet packets.
     *
     * Note: [connectInternet] must be running for this to have an effect.
     */
    fun startInternetTransmit() {
        mutableInternetTransmitState.value = true
    }

    /**
     * Stop transmitting internet packets.
     */
    fun stopInternetTransmit() {
        mutableInternetTransmitState.value = false
    }

    /**
     * Start listening to audio packets.
     */
    private suspend fun listenForPackets() {
        if (mutableAudioListenState.value) {
            logger.error("Tried to listen for audio packets while already active")
            return
        }
        try {
            mutableAudioListenState.value = true
            drivers.afskDriver.connect()
        } finally {
            logger.trace("Audio service cancelling")
            mutableAudioListenState.value = false
        }
    }

    /**
     * Start listening to internet packets.
     */
    private suspend fun listenForInternetPackets() {
        if (mutableInternetListenState.value) {
            logger.error("Tried to listen for internet packets while already active")
            return
        }
        try {
            mutableInternetListenState.value = true
            drivers.internetDriver.connect()
        } catch (e: Throwable) {
            logger.error("Internet listen terminated", e)
        } finally {
            logger.trace("Internet service cancelling")
            mutableInternetListenState.value = false
        }
    }

    /**
     * Transmit an APRS packet to the specified driver at the configured interval.
     */
    private suspend fun transmitLoop(driver: PacketDriver) {
        settings.observeData(connectionSettings.address)
            .filterNotNull()
            .combine(settings.observeData(transmitSettings.digipath)) { callsign, path ->
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
            .combine(settings.observeData(transmitSettings.symbol)) { prototype, symbol ->
                prototype.copy(symbol = symbol)
            }
            .combine(settings.observeString(transmitSettings.comment)) { prototype, comment ->
                prototype.copy(comment = comment)
            }
            .combine(settings.observeData(transmitSettings.destination)) { prototype, destination ->
                prototype.copy(destination = destination)
            }
            .combine(settings.observeData(transmitSettings.minRate)) { prototype, rate ->
                prototype.copy(minRate = rate)
            }
            .combine(settings.observeData(transmitSettings.maxRate)) { prototype, rate ->
                prototype.copy(maxRate = rate)
            }
            .combine(settings.observeData(transmitSettings.distance)) { prototype, distance ->
                prototype.copy(distance = distance)
            }
            .flatMapLatest { prototype ->
                locationAccess.observeLocationChanges(prototype.maxRate, prototype.distance)
                    .map { prototype to it }
            }
            .combineTriple(combinedTransmitState)
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
