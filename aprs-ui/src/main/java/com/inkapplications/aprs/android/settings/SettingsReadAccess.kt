package com.inkapplications.aprs.android.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Provides access to key/value primitive preferences for the application.
 */
interface SettingsReadAccess {
    fun observeStringState(setting: StringSetting): Flow<String?>
    fun observeIntState(setting: IntSetting): Flow<Int?>
    fun observeBooleanState(setting: BooleanSetting): Flow<Boolean?>
}

fun SettingsReadAccess.observeString(setting: StringSetting): Flow<String> {
    return observeStringState(setting).map { it ?: setting.defaultValue }
}

fun SettingsReadAccess.observeInt(setting: IntSetting): Flow<Int> {
    return observeIntState(setting).map { it ?: setting.defaultValue }
}

fun SettingsReadAccess.observeBoolean(setting: BooleanSetting): Flow<Boolean> {
    return observeBooleanState(setting).map { it ?: setting.defaultValue }
}
