package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.index.LogIndexState
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.MapState
import com.inkapplications.ack.android.map.MarkerViewStateFactory
import com.inkapplications.ack.android.map.ZoomLevels
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.android.station.StationSettings
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.mapItems
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    private val packetStorage: PacketStorage,
    private val stateFactory: LogItemViewStateFactory,
    private val markerViewStateFactory: MarkerViewStateFactory,
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
                .mapItems { stateFactory.create(it.id, it.parsed, metric) }
        }
        .map { if (it.isEmpty()) LogIndexState.Empty else LogIndexState.LogList(it) }

    fun stateEvents(id: Long): Flow<LogDetailData> {
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
    }

    fun mapState(id: Long): Flow<MapState> {
        return packetStorage.findById(id)
            .filterNotNull()
            .map { packet ->
                MapState(
                    markers = markerViewStateFactory.create(packet)?.let { listOf(it) }.orEmpty(),
                    mapCameraPosition = (packet.parsed.data as? Mapable)?.coordinates
                        ?.let { MapCameraPosition(it, ZoomLevels.ROADS) }
                        ?: CameraPositionDefaults.unknownLocation,
                )
            }
    }
}
