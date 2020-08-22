package com.inkapplications.aprs.android.settings

/**
 * Provides access to storing key/value preferences for the application.
 */
interface SettingsWriteAccess {
    fun setString(setting: StringSetting, value: String)
    fun setInt(setting: IntSetting, value: Int)
}