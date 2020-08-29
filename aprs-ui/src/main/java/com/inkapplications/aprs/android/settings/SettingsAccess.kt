package com.inkapplications.aprs.android.settings

import com.inkapplications.kotlin.mapEach
import com.xwray.groupie.kotlinandroidextensions.Item
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

    fun getSettingsAsItems(
        onBooleanChange: (BooleanSetting, Boolean) -> Unit
    ): Flow<List<Item>> = showingAdvanced
        .map { showAdvanced ->
            settingsProvider.settings.filter { showAdvanced || !it.advanced }
        }
        .mapEach { setting ->
            when (setting) {
                is StringSetting -> settingValues.observeString(setting)
                    .map { StringSettingViewModel(setting, it) }
                    .map { StringSettingItem(it, setting) }
                is IntSetting -> settingValues.observeInt(setting)
                    .map { IntSettingViewModel(setting, it) }
                    .map { IntSettingItem(it, setting) }
                is BooleanSetting -> settingValues.observeBoolean(setting)
                    .map { BooleanSettingViewModel(setting, it) }
                    .map { BooleanSettingItem(it, setting, onBooleanChange) }
            }
        }
        .flatMapLatest {
            combine(*it.toTypedArray()) { it.toList() }
        }

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
