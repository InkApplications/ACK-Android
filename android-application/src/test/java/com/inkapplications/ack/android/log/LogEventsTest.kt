package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.*
import com.inkapplications.ack.android.input.ZeroInclusivePositiveIntegerValidator
import com.inkapplications.ack.android.log.index.LogIndexState
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.details.LogDetailsState
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.station.StationSettings
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.android.extensions.ViewModelFactory
import kimchi.logger.EmptyLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LogEventsTest {
    private val stationSettings = StationSettings(ParrotStringResources, ZeroInclusivePositiveIntegerValidator(ParrotStringResources))

    private val settingsWithUnknownFiltered = object: SettingsReadAccess by StubSettings {
        override fun observeBooleanState(setting: BooleanSetting) = flow {
            emit(true)
        }
    }
    private val dummyStateFactory = object: LogItemViewModelFactory {
        override fun create(id: Long, packet: AprsPacket, metric: Boolean): LogItemViewModel {
            return LogItemViewModel(0, Callsign(""), "", "", null)
        }
    }
    private val stubLogDetailsFactory = object: ViewModelFactory<LogDetailData, LogDetailsState.LogDetailsViewModel> {
        override fun create(data: LogDetailData): LogDetailsState.LogDetailsViewModel = TODO()
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
            logDetailsFactory = stubLogDetailsFactory,
            stationSettings = stationSettings,
            logger = EmptyLogger,
        )

        val result = events.logIndexState.first()

        assertTrue(result is LogIndexState.LogList)
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
            logDetailsFactory = stubLogDetailsFactory,
            stationSettings = stationSettings,
            logger = EmptyLogger,
        )

        val result = events.logIndexState.first()

        assertTrue(result is LogIndexState.Empty)
    }
}
