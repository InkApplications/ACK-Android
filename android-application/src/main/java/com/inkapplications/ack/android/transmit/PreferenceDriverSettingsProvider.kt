package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeData
import com.inkapplications.ack.android.settings.observeInt
import com.inkapplications.ack.android.settings.observeString
import com.inkapplications.ack.data.AfskModulationConfiguration
import com.inkapplications.ack.data.ConnectionConfiguration
import com.inkapplications.ack.data.drivers.DriverSettingsProvider
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Reusable
class PreferenceDriversSettingsProvider @Inject constructor(
    settings: SettingsReadAccess,
    connectionSettings: ConnectionSettings,
    transmitSettings: TransmitSettings,
    logger: KimchiLogger,
): DriverSettingsProvider {
    override val internetServiceConfiguration: Flow<ConnectionConfiguration> = settings.observeData(connectionSettings.address)
        .filterNotNull()
        .map { ConnectionConfiguration(it) }
        .combine(settings.observeData(connectionSettings.passcode)) { settings, passcode ->
            settings.copy(passcode = passcode?.value ?: -1)
        }
        .combine(settings.observeString(connectionSettings.server)) { settings, server ->
            settings.copy(host = server)
        }
        .combine(settings.observeInt(connectionSettings.port)) { settings, port ->
            settings.copy(port = port)
        }
        .combine(settings.observeData(connectionSettings.radius)) { settings, radius ->
            settings.copy(searchRadius = radius)
        }
        .onEach { logger.debug("New Internet Service Configuration Received: $it") }

    override val afskConfiguration: Flow<AfskModulationConfiguration> = settings.observeData(transmitSettings.preamble)
        .combine(settings.observeData(transmitSettings.volume)) { preamble, volume ->
            AfskModulationConfiguration(
                preamble = preamble,
                volume = volume,
            )
        }
}
