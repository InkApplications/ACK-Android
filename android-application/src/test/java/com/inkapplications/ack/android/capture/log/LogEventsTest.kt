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
import kotlin.test.assertTrue

class LogEventsTest {
    private val settingsWithUnknownFiltered = object: SettingsReadAccess by StubSettings {
        override fun observeBooleanState(setting: BooleanSetting) = flow {
            emit(true)
        }
    }
    private val dummyStateFactory = object: LogItemViewModelFactory {
        override fun create(id: Long, packet: AprsPacket, metric: Boolean): LogItemViewModel {
            return LogItemViewModel(0, "", "", null)
        }
    }

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
        val localeSettings = LocaleSettings(ParrotStringResources)
        val logSettings = LogSettings(ParrotStringResources)
        val events = LogEvents(
            packetStorage = packetStorage,
            stateFactory = dummyStateFactory,
            settings = settingsWithUnknownFiltered,
            localeSettings = localeSettings,
            logSettings = logSettings,
        )

        val result = events.logScreenState.first()

        assertTrue(result is LogScreenState.LogList)
        assertEquals(1, result.logs.size)
    }

    @Test
    fun emptyList() = runTest {
        val packetStorage = object: PacketStorage by PacketStorageStub {
            override fun findRecent(count: Int): Flow<List<CapturedPacket>> = flow {
                emit(listOf())
            }
        }
        val localeSettings = LocaleSettings(ParrotStringResources)
        val logSettings = LogSettings(ParrotStringResources)
        val events = LogEvents(
            packetStorage = packetStorage,
            stateFactory = dummyStateFactory,
            settings = settingsWithUnknownFiltered,
            localeSettings = localeSettings,
            logSettings = logSettings,
        )

        val result = events.logScreenState.first()

        assertTrue(result is LogScreenState.Empty)
    }
}
