package com.inkapplications.ack.android.settings

import org.junit.Assert.assertTrue
import org.junit.Test

class CompositeSettingsProviderTest {
    @Test
    fun getSettings() {
        val first = object: SettingsProvider {
            override val settings: List<Setting> = listOf(stringSetting)
        }
        val second = object: SettingsProvider {
            override val settings: List<Setting> = listOf(intSetting)
        }
        val provider = CompositeSettingsProvider(setOf(first, second))

        val result = provider.settings

        assertTrue("Both settings are in combined result", stringSetting in result)
        assertTrue("Both settings are in combined result", intSetting in result)
    }

    @Test
    fun emptySettings() {
        val provider = CompositeSettingsProvider(setOf())

        val result = provider.settings

        assertTrue("No settings providers results in empty set", result.isEmpty())
    }
}
