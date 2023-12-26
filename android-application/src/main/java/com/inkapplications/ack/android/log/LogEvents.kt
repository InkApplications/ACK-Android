package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.log.details.LogDetailData
import com.inkapplications.ack.android.log.index.LogIndexData
import com.inkapplications.ack.android.map.*
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.android.station.StationSettings
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.coroutines.combinePair
import com.inkapplications.coroutines.filterItems
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
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

    fun stateEvents(id: CaptureId): Flow<LogDetailData> {
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

    fun mapState(id: CaptureId): Flow<MapState> {
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
