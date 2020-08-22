package com.inkapplications.aprs.android.settings

import kotlinx.coroutines.flow.Flow

/**
 * Provides access to key/value primitive preferences for the application.
 */
interface SettingsReadAccess {
    fun observeString(setting: StringSetting): Flow<Result<String>>
    fun observeInt(setting: IntSetting): Flow<Result<Int>>
}