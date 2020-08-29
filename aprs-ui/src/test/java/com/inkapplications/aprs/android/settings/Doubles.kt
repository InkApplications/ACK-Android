package com.inkapplications.aprs.android.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

val stringSetting = StringSetting("test.string", "test string", "category", "default")
val intSetting = IntSetting("test.int", "test int", "category", 1234)

object SettingsWriteAccessStub: SettingsWriteAccess {
    override fun setString(setting: StringSetting, value: String) = Unit
    override fun setInt(setting: IntSetting, value: Int) = Unit
}

object SettingsReadAccessStub: SettingsReadAccess {
    override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf("foo - ${setting.key}")
    override fun observeIntState(setting: IntSetting): Flow<Int?> = flowOf(1)
}

object SettingsProviderStub: SettingsProvider {
    override val settings: List<Setting> = listOf(stringSetting, intSetting)
}
