package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.AprsAccessStub
import com.inkapplications.aprs.android.ParrotStringResources
import com.inkapplications.aprs.android.StubSettings
import com.inkapplications.aprs.android.TestPackets
import com.inkapplications.aprs.android.TestPackets.toTestCapturedPacket
import com.inkapplications.aprs.android.locale.LocaleSettings
import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.AprsPacket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LogEventsTest {
    @Test
    fun unknownFilter() = runBlockingTest {
        val aprsAccess = object: AprsAccess by AprsAccessStub {
            override fun findRecent(count: Int): Flow<List<CapturedPacket>> = flow {
                emit(listOf(
                    TestPackets.unknown.toTestCapturedPacket(),
                    TestPackets.weather.toTestCapturedPacket(),
                ))
            }
        }
        val stateFactory = object: LogItemViewModelFactory {
            override fun create(id: Long, packet: AprsPacket, metric: Boolean): LogItemViewModel {
                return LogItemViewModel(0, "", "", null)
            }
        }
        val localeSettings = LocaleSettings(ParrotStringResources)
        val logSettings = LogSettings(ParrotStringResources)
        val settings = object: SettingsReadAccess by StubSettings {
            override fun observeBooleanState(setting: BooleanSetting) = flow {
                emit(true)
            }
        }
        val events = LogEvents(
            aprs = aprsAccess,
            stateFactory = stateFactory,
            settings = settings,
            localeSettings = localeSettings,
            logSettings = logSettings,
        )

        val result = events.logViewModels.first()

        assertEquals(1, result.size)
    }
}
