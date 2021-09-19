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
    suspend fun listenForPackets() {
        aprs.listenForAudioPackets().collect {
            logger.debug("APRS Packet Recorded: $it")
        }
    }

    suspend fun listenForInternetPackets() {
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
    }
}
