package com.inkapplications.aprs.android.settings

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsAccessTest {
    @Test
    fun items() = runBlockingTest {
        val access = SettingsAccess(
            SettingsProviderStub,
            SettingsReadAccessStub,
            SettingsWriteAccessStub
        )

        val result = access.getSettingsAsItems { _, _ -> }.first()

        assertEquals(2, result.size, "Provider should yield a result for each Setting.")
        val (first, second) = result
        assertTrue(first is StringSettingItem, "First setting should be a string item.")
        assertEquals(stringSetting, first.setting, "Settings should not be transformed.")
        assertEquals("test string", first.viewModel.name, "Setting name should be reflected in viewmodel.")
        assertEquals("foo - test.string", first.viewModel.value, "Setting value is provided by read access stub.")

        assertTrue(second is IntSettingItem)
        assertEquals(intSetting, second.setting, "Settings should not be transformed.")
        assertEquals("test int", second.viewModel.name, "Setting name should be reflected in viewmodel.")
        assertEquals("1", second.viewModel.value, "Setting value is provided by read access stub.")
    }

    @Test
    fun advancedHidden() = runBlockingTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                stringSetting,
                stringSetting.copy(key = "advanced", advanced = true)
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub
        )

        val result = access.getSettingsAsItems { _, _ -> }.first()

        assertEquals(1, result.size, "List is filtered to only simple settigns.")
        assertEquals(stringSetting, (result.first() as StringSettingItem).setting, "Advanced settings are removed from settings list.")
    }

    @Test
    fun advancedShown() = runBlockingTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                stringSetting,
                stringSetting.copy(key = "advanced", advanced = true)
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub
        )

        access.showAdvancedSettings()
        val result = access.getSettingsAsItems { _, _ -> }.first()

        assertEquals(2, result.size, "List is filtered to only simple settigns.")
        assertEquals(stringSetting, (result[0] as StringSettingItem).setting, "Advanced settings are removed from settings list.")
        assertEquals("advanced", (result[1] as StringSettingItem).setting.key, "Advanced settings are removed from settings list.")
    }
}
