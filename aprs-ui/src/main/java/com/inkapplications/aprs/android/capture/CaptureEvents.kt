package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.android.connection.ConnectionSettings
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeInt
import com.inkapplications.aprs.android.settings.observeString
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.ConnectionConfiguration
import com.inkapplications.karps.structures.toAddress
import dagger.Reusable
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.of
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class CaptureEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val settings: SettingsReadAccess,
    private val connectionSettings: ConnectionSettings,
    private val logger: KimchiLogger,
) {
    private val audioListenState = MutableStateFlow(false)
    private val internetListenState = MutableStateFlow(false)

    val screenState = audioListenState
        .map { CaptureScreenViewModel(recordingEnabled = it) }
        .combine(internetListenState) { viewModel, state ->
            viewModel.copy(internetServiceEnabled = state)
        }
        .combine(settings.observeString(connectionSettings.callsign)) { viewModel, callsign ->
            viewModel.copy(internetServiceVisible = callsign.isNotBlank())
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
