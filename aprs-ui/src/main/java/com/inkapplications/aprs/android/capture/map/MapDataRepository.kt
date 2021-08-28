package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.capture.log.LogItemState
import com.inkapplications.aprs.android.capture.log.LogStateFactory
import com.inkapplications.aprs.android.locale.LocaleSettings
import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.observeBoolean
import com.inkapplications.aprs.android.settings.observeInt
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.kotlin.filterEachNotNull
import com.inkapplications.kotlin.mapEach
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Aggregate data needed to display the map.
 */
@Reusable
class MapDataRepository @Inject constructor(
    private val logger: KimchiLogger,
    private val aprs: AprsAccess,
    private val symbolFactory: SymbolFactory,
    private val logStateFactory: LogStateFactory,
    private val settings: SettingsReadAccess,
    private val mapSettings: MapSettings,
    private val localeSettings: LocaleSettings,
) {
    fun findMarkers(): Flow<Collection<MarkerViewModel>> {
        return settings.observeInt(mapSettings.pinCount)
            .flatMapLatest { pinCount ->
                aprs.findRecent(pinCount)
                    .map { it.distinctBy { it.data.source } }
                    .mapEach { packet ->
                        when (val data = packet.data) {
                            is AprsPacket.Position -> MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))
                            else -> null
                        }
                    }
                    .filterEachNotNull()
                    .onEach { logger.info("New set of ${it.size} map markers.") }
            }
    }

    fun findLogItem(id: Long): Flow<LogItemState?> {
        return settings.observeBoolean(localeSettings.preferMetric).flatMapLatest { metric ->
            aprs.findById(id).map { it?.let { logStateFactory.create(it.id, it.data, metric) } }
        }
    }
}

