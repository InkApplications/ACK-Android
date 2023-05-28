package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.license.LicensePromptFieldValues
import com.inkapplications.ack.structures.station.toStationAddress
import com.inkapplications.coroutines.mapEach
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Provide the settings screen with application-wide settings info.
 */
class SettingsAccess @Inject constructor(
    private val settingsProvider: SettingsProvider,
    private val settingValues: SettingsReadAccess,
    private val settingsStorage: SettingsWriteAccess,
    private val connectionSettings: ConnectionSettings,
) {
    private val showingAdvanced = MutableStateFlow(false)

    val settingsStateGrouped = showingAdvanced
        .map { showAdvanced ->
            settingsProvider.settings.filter { showAdvanced || !it.advanced }
        }
        .map {
            it.groupBy { it.categoryName }.map { (key, settings) ->
                settings.map { setting ->
                        when (setting) {
                            is StringSetting -> settingValues.observeString(setting)
                                .map { SettingState.StringState(setting, it) }
                            is IntSetting -> settingValues.observeInt(setting)
                                .map { SettingState.IntState(setting, it) }
                            is BooleanSetting -> settingValues.observeBoolean(setting)
                                .map { SettingState.BooleanState(setting, it) }
                        }
                    }
                    .let { combine(*it.toTypedArray()) { it.toList() } }
                    .map { SettingsGroup(key, it) }

            }
        }
        .flatMapLatest {
            combine(*it.toTypedArray()) { it.toList() }
        }
        .mapEach {
            it.copy(settings = it.settings.sortedBy { it.setting.name })
        }
        .map { it.sortedBy { it.name } }

    val settingsViewState = settingsStateGrouped
        .map { SettingsViewState(settingsList = it) }
        .combine(settingValues.observeData(connectionSettings.address)) { viewModel, callsign ->
            viewModel.copy(callsignText = callsign?.toString())
        }
        .combine(settingValues.observeInt(connectionSettings.passcode)) { viewModel, passcode ->
            viewModel.copy(verified = passcode != -1)
        }

    val licensePromptFieldValues: Flow<LicensePromptFieldValues> = settingValues.observeData(connectionSettings.address)
        .combine(settingValues.observeInt(connectionSettings.passcode)) { callsign, passcode ->
            LicensePromptFieldValues(callsign?.toString().orEmpty(), passcode.takeIf { it != -1 }?.toString().orEmpty())
        }

    fun setLicense(values: LicensePromptFieldValues) {
        settingsStorage.setData(connectionSettings.address, values.callsign.trim().takeIf { it.isNotEmpty() }?.toStationAddress())
        settingsStorage.setInt(connectionSettings.passcode, values.passcode.trim().toIntOrNull() ?: -1)
    }

    fun showAdvancedSettings() {
        showingAdvanced.value = true
    }

    fun updateInt(key: String, value: Int) {
        val setting = settingsProvider.settings.find { it.key == key } as? IntSetting
            ?: throw IllegalArgumentException("Unknown Int Setting: <$key>")
        settingsStorage.setInt(setting, value)
    }

    fun updateString(key: String, value: String) {
        val setting = settingsProvider.settings.find { it.key == key } as? StringSetting
                ?: throw IllegalArgumentException("Unknown String Setting: <$key>")
        settingsStorage.setString(setting, value)
    }

    fun updateBoolean(key: String, value: Boolean) {
        val setting = settingsProvider.settings.find { it.key == key } as? BooleanSetting
            ?: throw IllegalArgumentException("Unknown Boolean Setting: <$key>")
        settingsStorage.setBoolean(setting, value)
    }
}
