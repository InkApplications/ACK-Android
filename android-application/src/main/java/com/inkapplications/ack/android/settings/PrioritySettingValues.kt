package com.inkapplications.ack.android.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

    /**
     * Merge a list of flows taking the first flow's latest result that matches a [predicate]
     */
    private inline fun <reified T> List<Flow<T>>.flattenFirst(crossinline predicate: (T) -> Boolean): Flow<T?> = combine(*this.toTypedArray()) {
        it.firstOrNull { predicate(it) }
    }
}
