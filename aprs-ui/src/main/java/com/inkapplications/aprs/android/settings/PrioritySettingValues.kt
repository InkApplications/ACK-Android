package com.inkapplications.aprs.android.settings

import com.inkapplications.kotlin.flattenFirst
import com.inkapplications.kotlin.mapNullToDefault
import kotlinx.coroutines.flow.Flow
import java.lang.IllegalStateException

/**
 * Provides read access to a collection of settings provider by taking the first one with a
 * successful result.
 */
class PrioritySettingValues(private vararg val delegates: SettingsReadAccess): SettingsReadAccess {
    override fun observeStringState(setting: StringSetting): Flow<String?> {
        return delegates.map { it.observeStringState(setting) }.flattenFirst { it != null }
    }

    override fun observeIntState(setting: IntSetting): Flow<Int?> {
        return delegates.map { it.observeIntState(setting) }.flattenFirst { it != null }
    }
}