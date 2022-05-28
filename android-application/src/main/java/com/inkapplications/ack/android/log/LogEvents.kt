package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.details.LogDetailsState
import com.inkapplications.ack.android.log.index.LogIndexState
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.android.station.StationSettings
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.ViewModelFactory
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.mapEach
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    private val packetStorage: PacketStorage,
    private val stateFactory: LogItemViewModelFactory,
    private val logDetailsFactory: ViewModelFactory<LogDetailData, LogDetailsState.LogDetailsViewModel>,
    private val settings: SettingsReadAccess,
    private val localeSettings: LocaleSettings,
    private val stationSettings: StationSettings,
    logSettings: LogSettings,
    private val logger: KimchiLogger,
) {
    val logIndexState = settings.observeBoolean(localeSettings.preferMetric)
        .combinePair(settings.observeBoolean(logSettings.filterUnknown))
        .flatMapLatest { (metric, filterUnknown) ->
            packetStorage.findRecent(500)
                .map {
                    if (filterUnknown) it.filter { it.parsed.data !is PacketData.Unknown } else it
                }
                .mapEach { stateFactory.create(it.id, it.parsed, metric) }
        }
        .map { if (it.isEmpty()) LogIndexState.Empty else LogIndexState.LogList(it) }

    fun stateEvents(id: Long): Flow<LogDetailsState> {
        logger.trace("Observing packet: $id")
        return packetStorage.findById(id)
            .filterNotNull()
            .combine(settings.observeBoolean(localeSettings.preferMetric)) { packet, metric ->
                LogDetailData(
                    packet = packet,
                    metric = metric,
                )
            }
            .combine(settings.observeBoolean(stationSettings.showDebugData)) { data, debugData ->
                data.copy(debug = debugData)
            }
            .map { logDetailsFactory.create(it) }
            .onEach { logger.debug("New ViewModel Created: $it") }
    }
}
