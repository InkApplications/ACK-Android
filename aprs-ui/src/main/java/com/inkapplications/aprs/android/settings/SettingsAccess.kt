package com.inkapplications.aprs.android.settings

import com.xwray.groupie.kotlinandroidextensions.Item
import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    val settingItems: Flow<List<Item>> = settingsProvider.settings
        .map { setting ->
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
        .let {
            combine(*it.toTypedArray()) { it.toList() }
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