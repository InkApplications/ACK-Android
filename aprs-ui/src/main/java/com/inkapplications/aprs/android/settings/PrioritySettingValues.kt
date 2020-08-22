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
    override fun observeString(setting: StringSetting): Flow<Result<String>> {
        return delegates.map { it.observeString(setting) }.requireTakeFirst(setting.key)
    }

    override fun observeInt(setting: IntSetting): Flow<Result<Int>> {
        return delegates.map { it.observeInt(setting) }.requireTakeFirst(setting.key)
    }

    private fun <T> List<Flow<Result<T>>>.requireTakeFirst(key: String): Flow<Result<T>> {
        return flattenFirst { it.isSuccess }.mapNullToDefault(Result.failure(IllegalStateException("No value found for setting: <$key>")))
    }
}