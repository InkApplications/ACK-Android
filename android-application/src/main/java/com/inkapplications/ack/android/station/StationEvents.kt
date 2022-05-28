package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.log.LogItemViewModelFactory
import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
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
    private val stationInsightViewModelFactory: StationInsightViewModelFactory,
    private val logItemViewModelFactory: LogItemViewModelFactory,
    private val settings: SettingsReadAccess,
    private val localeSettings: LocaleSettings,
    private val logger: KimchiLogger = EmptyLogger,
)  {
    suspend fun packet(id: Long) = aprs.findById(id).first()

    fun stationState(callsign: Callsign): Flow<StationViewState> {
        logger.trace("Observing Callsign: $callsign")
        return aprs.findBySource(callsign)
            .combine(settings.observeBoolean(localeSettings.preferMetric)) { packets, metric ->
                StationData(
                    packets = packets,
                    metric = metric,
                )
            }
            .map {  data ->
                StationViewState.Loaded(
                    insight = stationInsightViewModelFactory.create(data),
                    packets = data.packets.map { packet ->
                        logItemViewModelFactory.create(packet.id, packet.parsed, data.metric)
                    }
                )
            }
    }
}
