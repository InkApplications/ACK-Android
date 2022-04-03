package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.android.extensions.StubStringResources
import com.inkapplications.ack.android.connection.ConnectionSettings
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
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.settingsStateGrouped.first().map { it.settings }.flatten()

        assertEquals(2, result.size, "Provider should yield a result for each Setting.")
        val (first, second) = result

        assertTrue(first is SettingState.IntState)
        assertEquals(intSetting.key, first.setting.key, "Settings key is preserved.")
        assertEquals(intSetting.name, first.setting.name, "Settings name is preserved.")
        assertEquals(1, first.value, "Setting value is provided by read access stub.")

        assertTrue(second is SettingState.StringState, "First setting should be a string item.")
        assertEquals(stringSetting.key, second.setting.key, "Settings key is preserved.")
        assertEquals(stringSetting.name, second.setting.name, "Settings name is preserved.")
        assertEquals("foo - test.string", second.value, "Setting value is provided by read access stub.")
    }

    @Test
    fun advancedHidden() = runBlockingTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                stringSetting,
                StringSetting(key = "advanced", advanced = true, name = "advanced", categoryName = stringSetting.categoryName, defaultValue = "advanced")
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.settingsStateGrouped.first().map { it.settings }.flatten()

        assertEquals(1, result.size, "List is filtered to only simple settings.")
        assertEquals(stringSetting.key, (result.first() as SettingState.StringState).setting.key, "Advanced settings are removed from settings list.")
    }

    @Test
    fun advancedShown() = runBlockingTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                stringSetting,
                StringSetting(key = "advanced", advanced = true, name = "zz_advanced", categoryName = stringSetting.categoryName, defaultValue = "advanced")
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        access.showAdvancedSettings()
        val result = access.settingsStateGrouped.first().map { it.settings }.flatten()

        assertEquals(2, result.size, "List is filtered to only simple settigns.")
        assertEquals(stringSetting.key, (result[0] as SettingState.StringState).setting.key, "Standard settings are still shown.")
        assertEquals("advanced", (result[1] as SettingState.StringState).setting.key, "Advanced settings are removed from settings list.")
    }

    @Test
    fun categorized() = runBlockingTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                StringSetting(key = "B1", categoryName = "B", name = "B1", defaultValue = "test"),
                StringSetting(key = "C1", categoryName = "C", name = "C1", defaultValue = "test"),
                StringSetting(key = "A1", categoryName = "A", name = "A1", defaultValue = "test"),
                StringSetting(key = "B2", categoryName = "B", name = "B2", defaultValue = "test"),
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        access.showAdvancedSettings()
        val result = access.settingsStateGrouped.first()
        assertEquals(3, result.size, "Settings are broken into categories by name")

        val (first, second, third) = result

        assertEquals("A", first.name, "Categories are sorted by name")
        assertEquals(1, first.settings.size, "Settings are kept in their category")
        assertEquals("A1", first.settings[0].setting.key, "Settings are kept in their category")

        assertEquals("B", second.name, "Categories are sorted by name")
        assertEquals(2, second.settings.size, "Settings are kept in their category")
        assertEquals("B1", second.settings[0].setting.key, "Settings are kept in their category")
        assertEquals("B2", second.settings[1].setting.key, "Settings are kept in their category")

        assertEquals("C", third.name, "Categories are sorted by name")
        assertEquals(1, third.settings.size, "Settings are kept in their category")
        assertEquals("C1", third.settings[0].setting.key, "Settings are kept in their category")
    }

    @Test
    fun sorted() = runBlockingTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                StringSetting(key = "3", name = "C", categoryName = "test", defaultValue = "test"),
                StringSetting(key = "4", name = "D", categoryName = "test", defaultValue = "test"),
                StringSetting(key = "1", name = "A", categoryName = "test", defaultValue = "test"),
                StringSetting(key = "2", name = "B", categoryName = "test", defaultValue = "test"),
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        access.showAdvancedSettings()
        val result = access.settingsStateGrouped.first().first().settings.map { it.setting.name }
        assertEquals(listOf("A", "B", "C", "D"), result, "Settings are sorted by name")

    }
}
