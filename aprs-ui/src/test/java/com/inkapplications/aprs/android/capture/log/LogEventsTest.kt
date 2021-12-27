package com.inkapplications.aprs.android.capture.log

import com.inkapplications.aprs.android.*
import com.inkapplications.aprs.android.locale.LocaleSettings
import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.PacketData
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
                    PacketData.Unknown(body = "").toTestPacket().toTestCapturedPacket(),
                    PacketData.Weather().toTestPacket().toTestCapturedPacket(),
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
