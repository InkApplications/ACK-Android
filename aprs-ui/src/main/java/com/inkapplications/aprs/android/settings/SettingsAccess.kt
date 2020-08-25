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

    val settingItems: Flow<List<Item>> = showingAdvanced
        .map { showAdvanced ->
            settingsProvider.settings.filter { showAdvanced || !it.advanced }
        }
        .mapEach { setting ->
            when (setting) {
                is StringSetting -> settingValues.observeStringState(setting).map {
                    SettingViewModel(
                        name = setting.name,
                        value = it.orEmpty()
                    ).let { SettingStringItem(it, setting) }
                }
                is IntSetting -> settingValues.observeIntState(setting).map {
                    SettingViewModel(
                        name = setting.name,
                        value = it?.toString().orEmpty()
                    ).let { SettingIntItem(it, setting) }
                }
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
}