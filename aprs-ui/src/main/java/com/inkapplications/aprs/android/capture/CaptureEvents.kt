package com.inkapplications.aprs.android.capture

import com.inkapplications.android.extensions.location.LocationAccess
import com.inkapplications.aprs.android.connection.ConnectionSettings
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeInt
import com.inkapplications.aprs.android.settings.observeString
import com.inkapplications.aprs.android.transmit.TransmitSettings
import com.inkapplications.aprs.data.AfskModulationConfiguration
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.ConnectionConfiguration
import com.inkapplications.karps.structures.*
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.of
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@Singleton
class CaptureEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val settings: SettingsReadAccess,
    private val connectionSettings: ConnectionSettings,
    private val transmitSettings: TransmitSettings,
    private val locationAccess: LocationAccess,
    private val logger: KimchiLogger,
) {
    private val audioListenState = MutableStateFlow(false)
    private val internetListenState = MutableStateFlow(false)
    private val transmitState = MutableStateFlow(false)

    val screenState = audioListenState
        .map { CaptureScreenViewModel(recordingEnabled = it) }
        .combine(internetListenState) { viewModel, state ->
            viewModel.copy(internetServiceEnabled = state)
        }
        .combine(settings.observeString(connectionSettings.callsign)) { viewModel, callsign ->
            viewModel.copy(internetServiceVisible = callsign.isNotBlank())
        }
        .combine(transmitState) { viewModel, state ->
            viewModel.copy(transmitState = state)
        }

    suspend fun listenForPackets() {
        if (audioListenState.value) {
            logger.error("Tried to listen for audio packets while already active")
            return
        }
        try {
            audioListenState.value = true
            aprs.listenForAudioPackets().collect {
                logger.debug("APRS Packet Recorded: $it")
            }
        } finally {
            logger.trace("Audio service cancelling")
            audioListenState.value = false
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun transmitLoop() {
        try {
            transmitState.value = true
            settings.observeString(connectionSettings.callsign)
                .combine(settings.observeString(transmitSettings.digipath)) { callsign, path ->
                    TransmitPrototype(
                        path = path.split(',').map { Digipeater(it.toAddress()) },
                        destination = transmitSettings.destination.defaultValue.toAddress(),
                        callsign = callsign.toAddress(),
                        symbol = transmitSettings.symbol.defaultValue.let { symbolOf(it[0], it[1]) },
                        comment = transmitSettings.comment.defaultValue,
                        minRate = transmitSettings.minRate.defaultValue.let { Duration.minutes(it) },
                        maxRate = transmitSettings.maxRate.defaultValue.let { Duration.minutes(it) },
                        distance = transmitSettings.distance.defaultValue.let { Miles.of(it) },
                        afskConfiguration = AfskModulationConfiguration(
                            preamble = transmitSettings.preamble.defaultValue.let { Duration.milliseconds(it) },
                            volume = transmitSettings.volume.defaultValue.let { WholePercentage.of(it) },
                        ),
                    )
                }
                .combine(settings.observeString(transmitSettings.symbol)) { prototype, symbol ->
                    prototype.copy(symbol = symbolOf(symbol[0], symbol[1]))
                }
                .combine(settings.observeString(transmitSettings.comment)) { prototype, comment ->
                    prototype.copy(comment = comment)
                }
                .combine(settings.observeString(transmitSettings.destination)) { prototype, destination ->
                    prototype.copy(destination = destination.toAddress())
                }
                .combine(settings.observeInt(transmitSettings.minRate)) { prototype, rate ->
                    prototype.copy(minRate = Duration.minutes(rate))
                }
                .combine(settings.observeInt(transmitSettings.maxRate)) { prototype, rate ->
                    prototype.copy(maxRate = Duration.minutes(rate))
                }
                .combine(settings.observeInt(transmitSettings.distance)) { prototype, distance ->
                    prototype.copy(distance = Miles.of(distance))
                }
                .combine(settings.observeInt(transmitSettings.preamble)) { prototype, preamble ->
                    prototype.copy(
                        afskConfiguration = prototype.afskConfiguration.copy(preamble = Duration.Companion.milliseconds(preamble)),
                    )
                }
                .combine(settings.observeInt(transmitSettings.volume)) { prototype, volume ->
                    prototype.copy(
                        afskConfiguration = prototype.afskConfiguration.copy(volume = WholePercentage.of(volume)),
                    )
                }
                .flatMapLatest { prototype ->
                    locationAccess.observeLocationChanges(prototype.maxRate, prototype.distance)
                        .map { prototype to it }
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

                        aprs.transmitAudioPacket(packet, EncodingConfig(compression = EncodingPreference.Barred), prototype.afskConfiguration)
                        delay(prototype.minRate)
                    }
                }
        } finally {
            transmitState.value = false
        }
    }

    suspend fun listenForInternetPackets() {
        if (internetListenState.value) {
            logger.error("Tried to listen for internet packets while already active")
            return
        }
        try {
            internetListenState.value =  true
            settings.observeString(connectionSettings.callsign)
                .map { ConnectionConfiguration(it.toAddress()) }
                .combine(settings.observeInt(connectionSettings.passcode)) { settings, passcode ->
                    settings.copy(passcode = passcode)
                }
                .combine(settings.observeString(connectionSettings.server)) { settings, server ->
                    settings.copy(host = server)
                }
                .combine(settings.observeInt(connectionSettings.port)) { settings, port ->
                    settings.copy(port = port)
                }
                .combine(settings.observeInt(connectionSettings.radius)) { settings, radius ->
                    settings.copy(searchRadius = Meters.of(Kilo, radius))
                }
                .onEach { logger.debug("New Connection Settings Received: $it") }
                .collectLatest { settings ->
                    withContext(Dispatchers.IO) {
                        aprs.listenForInternetPackets(settings).collect {
                            logger.debug("APRS-IS Packet Collected: $it")
                        }
                    }
                }
        } finally {
            logger.trace("Internet service cancelling")
            internetListenState.value = false
        }
    }
}
