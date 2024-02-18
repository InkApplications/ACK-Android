package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.android.settings.observeInt
import com.inkapplications.ack.data.CaptureId
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.station.Callsign
import dagger.Reusable
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@Reusable
class StationEvents @Inject constructor(
    private val aprs: PacketStorage,
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
}
