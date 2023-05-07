package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.index.LogIndexData
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
import com.inkapplications.coroutines.filterItems
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class LogEvents @Inject constructor(
    private val packetStorage: PacketStorage,
    private val markerViewStateFactory: MarkerViewStateFactory,
    private val settings: SettingsReadAccess,
    private val localeSettings: LocaleSettings,
    private val stationSettings: StationSettings,
    logSettings: LogSettings,
    private val logger: KimchiLogger,
) {
    val logIndex = settings.observeBoolean(localeSettings.preferMetric)
        .combinePair(settings.observeBoolean(logSettings.filterUnknown))
        .flatMapLatest { (metric, filterUnknown) ->
            packetStorage.findRecent(500)
                .filterItems { !filterUnknown || it.parsed.data !is PacketData.Unknown }
                .map { LogIndexData(metric, it) }
        }

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
