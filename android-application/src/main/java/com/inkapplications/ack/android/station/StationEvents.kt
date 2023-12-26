package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.log.LogItemViewStateFactory
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.android.settings.observeInt
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.station.Callsign
import dagger.Reusable
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@Reusable
class StationEvents @Inject constructor(
    private val aprs: PacketStorage,
    private val stationInsightViewStateFactory: StationInsightViewStateFactory,
    private val logItemViewStateFactory: LogItemViewStateFactory,
    private val settings: SettingsReadAccess,
    private val localeSettings: LocaleSettings,
    private val stationSettings: StationSettings,
    private val logger: KimchiLogger = EmptyLogger,
)  {
    suspend fun packet(id: CaptureId) = aprs.findById(id).first()

    fun stationData(callsign: Callsign): Flow<StationData> {
        logger.trace("Observing Station Data for: $callsign")
        return settings.observeInt(stationSettings.recentStationEvents)
            .flatMapLatest { aprs.findBySource(callsign, it.toLong()) }
            .combine(settings.observeBoolean(localeSettings.preferMetric)) { packets, metric ->
                StationData(
                    packets = packets,
                    metric = metric,
                )
            }
    }

    fun stationState(callsign: Callsign): Flow<StationViewState> {
        logger.trace("Observing Callsign: $callsign")

        return settings.observeInt(stationSettings.recentStationEvents)
            .flatMapLatest { aprs.findBySource(callsign, it.toLong()) }
            .combine(settings.observeBoolean(localeSettings.preferMetric)) { packets, metric ->
                StationData(
                    packets = packets,
                    metric = metric,
                )
            }
            .map {  data ->
                StationViewState.Loaded(
                    insight = stationInsightViewStateFactory.create(data),
                    packets = data.packets.map { packet ->
                        logItemViewStateFactory.create(packet.id, packet.parsed, data.metric)
                    }
                )
            }
    }
}
