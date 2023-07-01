package com.inkapplications.ack.android.settings

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.ack.android.connection.ConnectionSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsAccessTest {
    @Test
    fun items() = runTest {
        val access = SettingsAccess(
            SettingsProviderStub,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.settingsGroups(SettingVisibility.Advanced).first()
        val list = result.settings.map { it.settings }.flatten()

        assertEquals(2, list.size, "Provider should yield a result for each Setting.")
        val (first, second) = list

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
    fun advancedHidden() = runTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                stringSetting,
                StringSetting(key = "advanced", visibility = SettingVisibility.Advanced, name = "advanced", categoryName = stringSetting.categoryName, defaultValue = "advanced")
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.settingsGroups(SettingVisibility.Visible).first()
        val list = result.settings.map { it.settings }.flatten()

        assertEquals(1, list.size, "List is filtered to only simple settings.")
        assertEquals(stringSetting.key, (list.first() as SettingState.StringState).setting.key, "Advanced settings are removed from settings list.")
    }

    @Test
    fun advancedShown() = runTest {
        val settingsProvider = object: SettingsProvider {
            override val settings: List<Setting> = listOf(
                stringSetting,
                StringSetting(key = "advanced", visibility = SettingVisibility.Advanced, name = "zz_advanced", categoryName = stringSetting.categoryName, defaultValue = "advanced")
            )
        }

        val access = SettingsAccess(
            settingsProvider,
            SettingsReadAccessStub,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.settingsGroups(SettingVisibility.Advanced).first()
        val list = result.settings.map { it.settings }.flatten()

        assertEquals(2, list.size, "List is filtered to only simple settigns.")
        assertEquals(stringSetting.key, (list[0] as SettingState.StringState).setting.key, "Standard settings are still shown.")
        assertEquals("advanced", (list[1] as SettingState.StringState).setting.key, "Advanced settings are removed from settings list.")
    }

    @Test
    fun categorized() = runTest {
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

        val result = access.settingsGroups(SettingVisibility.Advanced).first()

        assertEquals(3, result.settings.size, "Settings are broken into categories by name")

        val (first, second, third) = result.settings

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
    fun licensePromptFieldValues() = runTest {
        val fakeSettings = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> {
                return flow { emit("KE0YOG") }
            }

            override fun observeIntState(setting: IntSetting): Flow<Int?> {
                return flow { emit(12345) }
            }
        }
        val access = SettingsAccess(
            SettingsProviderStub,
            fakeSettings,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.licensePromptFieldValues.first()

        assertEquals("KE0YOG", result.callsign)
        assertEquals("12345", result.passcode)
    }

    @Test
    fun emptyLicensePromptFieldValues() = runTest {
        val fakeSettings = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> {
                return flow { emit(null) }
            }

            override fun observeIntState(setting: IntSetting): Flow<Int?> {
                return flow { emit(null) }
            }
        }
        val access = SettingsAccess(
            SettingsProviderStub,
            fakeSettings,
            SettingsWriteAccessStub,
            ConnectionSettings(ParrotStringResources),
        )

        val result = access.licensePromptFieldValues.first()

        assertEquals("", result.callsign)
        assertEquals("", result.passcode)
    }

    @Test
    fun sorted() = runTest {
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

        val result = access.settingsGroups(SettingVisibility.Advanced).first()
        val list = result.settings.map { it.settings.map { it.setting.name } }.flatten()

        assertEquals(listOf("A", "B", "C", "D"), list, "Settings are sorted by name")

    }
}
