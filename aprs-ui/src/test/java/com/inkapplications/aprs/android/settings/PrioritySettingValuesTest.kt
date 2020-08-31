package com.inkapplications.aprs.android.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrioritySettingValuesTest {
    @Test
    fun firstIsPrioritized() {
        val first = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf("first")
        }
        val second = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf("second")
        }
        val values = PrioritySettingValues(first, second)

        runBlocking {
            assertEquals("first", values.observeStringState(stringSetting).first())
        }
    }

    @Test
    fun secondIsUsed() {
        val first = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf(null)
        }
        val second = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf("second")
        }
        val values = PrioritySettingValues(first, second)

        runBlocking {
            assertEquals("second", values.observeStringState(stringSetting).first())
        }
    }

    @Test
    fun noSetting() {
        val first = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf(null)
        }
        val second = object: SettingsReadAccess by SettingsReadAccessStub {
            override fun observeStringState(setting: StringSetting): Flow<String?> = flowOf(null)
        }
        val values = PrioritySettingValues(first, second)

        runBlocking {
            assertNull(values.observeStringState(stringSetting).first())
        }
    }
}
