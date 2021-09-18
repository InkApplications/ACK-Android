package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.android.onboard.OnboardSettings
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeInt
import com.inkapplications.aprs.android.settings.observeString
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.ConnectionSettings
import com.inkapplications.karps.structures.toAddress
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Reusable
class CaptureEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val settings: SettingsReadAccess,
    private val onboardSettings: OnboardSettings,
    private val logger: KimchiLogger,
) {
    suspend fun listenForPackets() {
        aprs.listenForAudioPackets().collect {
            logger.debug("APRS Packet Recorded: $it")
        }
    }

    suspend fun listenForInternetPackets() {
        settings.observeString(onboardSettings.callsign)
            .map { ConnectionSettings(it.toAddress()) }
            .combine(settings.observeInt(onboardSettings.passcode)) { settings, passcode ->
                settings.copy(passcode = passcode)
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
