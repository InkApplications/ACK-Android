package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.license.LicensePromptFieldValues
import com.inkapplications.ack.structures.station.toStationAddress
import com.inkapplications.coroutines.mapItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    fun settingsGroups(visibility: SettingVisibility): Flow<SettingsListData> {
        return settingsProvider.settings
            .filter { it.visibility <= visibility }
            .groupBy { it.categoryName }
            .map { (key, settings) ->
                settings.map { setting ->
                    when (setting) {
                        is StringSetting -> settingValues.observeString(setting)
                            .map { SettingState.StringState(setting, it) }
                        is IntSetting -> settingValues.observeInt(setting)
                            .map { SettingState.IntState(setting, it) }
                        is BooleanSetting -> settingValues.observeBoolean(setting)
                            .map { SettingState.BooleanState(setting, it) }
                    }
                }.let {
                    combine(*it.toTypedArray()) { it.toList() }
                } .map {
                    SettingsGroup(key, it)
                }
            }
            .let {
                combine(*it.toTypedArray()) { it.toList() }
            }
            .mapItems {
                it.copy(settings = it.settings.sortedBy { it.setting.name })
            }
            .map {
                it.sortedBy { it.name }
            }
            .map {
                SettingsListData().apply {
                    this.settings = it
                    this.visibility = visibility
                }
            }
    }

    val licenseData = settingValues.observeData(connectionSettings.address)
        .combine(settingValues.observeData(connectionSettings.passcode)) { callsign, passcode ->
            LicenseData(callsign, passcode)
        }

    val licensePromptFieldValues: Flow<LicensePromptFieldValues> = settingValues.observeData(connectionSettings.address)
        .combine(settingValues.observeData(connectionSettings.passcode)) { callsign, passcode ->
            LicensePromptFieldValues(callsign?.toString().orEmpty(), passcode?.value?.toString().orEmpty())
        }

    fun setLicense(values: LicensePromptFieldValues) {
        settingsStorage.setData(connectionSettings.address, values.callsign.trim().takeIf { it.isNotEmpty() }?.toStationAddress())
        settingsStorage.setData(connectionSettings.passcode, Passcode(values.passcode.trim().toIntOrNull() ?: -1))
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
