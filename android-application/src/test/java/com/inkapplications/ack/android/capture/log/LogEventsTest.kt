package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LogEventsTest {
    @Test
    fun unknownFilter() = runTest {
        val packetStorage = object: PacketStorage by PacketStorageStub {
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
            packetStorage = packetStorage,
            stateFactory = stateFactory,
            settings = settings,
            localeSettings = localeSettings,
            logSettings = logSettings,
        )

        val result = events.logViewModels.first()

        assertEquals(1, result.size)
    }
}
