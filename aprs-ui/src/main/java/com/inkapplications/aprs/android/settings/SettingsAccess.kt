package com.inkapplications.aprs.android.settings

import com.inkapplications.coroutines.mapEach
import dagger.Reusable
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Provide the settings screen with application-wide settings info.
 */
@Reusable
class SettingsAccess @Inject constructor(
    private val settingsProvider: SettingsProvider,
    private val settingValues: SettingsReadAccess,
    private val settingsStorage: SettingsWriteAccess
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
                                .map { SettingState.StringState(setting.key, setting.name, it) }
                            is IntSetting -> settingValues.observeInt(setting)
                                .map { SettingState.IntState(setting.key, setting.name, it) }
                            is BooleanSetting -> settingValues.observeBoolean(setting)
                                .map { SettingState.BooleanState(setting.key, setting.name, it) }
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
            it.copy(settings = it.settings.sortedBy { it.name })
        }
        .map { it.sortedBy { it.name } }

    fun clearState() {
        showingAdvanced.value = false
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
