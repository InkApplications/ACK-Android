package com.inkapplications.ack.android.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Read access to settings that always returns a null result.
 *
 * This can be used for testing or as a stand-in for disabled services.
 */
object NullSettingsReadAccesss: SettingsReadAccess {
    override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf(null)
    override fun observeIntState(setting: IntSetting): Flow<Int?> = flowOf(null)
    override fun observeBooleanState(setting: BooleanSetting): Flow<Boolean?> = flowOf(null)
}
