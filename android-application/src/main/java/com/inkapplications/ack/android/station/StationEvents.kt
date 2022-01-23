package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.data.AprsAccess
import dagger.Reusable
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@Reusable
class StationEvents @Inject constructor(
    private val aprs: AprsAccess,
    private val stationViewModelFactory: StationViewModelFactory,
    private val settings: SettingsReadAccess,
    private val localeSettings: LocaleSettings,
    private val stationSettings: StationSettings,
    private val logger: KimchiLogger = EmptyLogger,
)  {
    suspend fun packet(id: Long) = aprs.findById(id).first()

    fun stateEvents(id: Long): Flow<StationViewModel> {
        logger.trace("Observing packet: $id")
        return aprs.findById(id)
            .combine(settings.observeBoolean(localeSettings.preferMetric)) { packet, metric ->
                packet to metric
            }
            .combine(settings.observeBoolean(stationSettings.showDebugData)) { (packet, metric), debugData ->
                stationViewModelFactory.create(packet, metric, debugData)
            }
            .onEach { logger.debug("New ViewModel Created: $it") }
    }
}