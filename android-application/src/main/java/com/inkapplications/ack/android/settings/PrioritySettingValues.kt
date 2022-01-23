package com.inkapplications.ack.android.settings

import com.inkapplications.kotlin.flattenFirst
import kotlinx.coroutines.flow.Flow

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

    override fun observeBooleanState(setting: BooleanSetting): Flow<Boolean?> {
        return delegates.map { it.observeBooleanState(setting) }.flattenFirst { it != null }
    }
}
