package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.settings.transformer.Transformer

/**
 * Provides access to storing key/value preferences for the application.
 */
interface SettingsWriteAccess {
    fun setString(setting: StringSetting, value: String)
    fun setInt(setting: IntSetting, value: Int)
    fun setBoolean(setting: BooleanSetting, value: Boolean)
}

fun <DATA, STORAGE> SettingsWriteAccess.setData(setting: TransformableSetting<DATA, STORAGE>, value: DATA) {
    return when (setting) {
        is IntBackedSetting -> setInt(setting, setting.transformer.toStorage(value))
        is StringBackedSetting -> setString(setting, setting.transformer.toStorage(value))
    }
}
