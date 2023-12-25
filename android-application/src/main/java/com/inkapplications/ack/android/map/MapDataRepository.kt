package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.locale.LocaleSettings
import com.inkapplications.ack.android.log.CombinedLogItemViewStateFactory
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.observeBoolean
import com.inkapplications.ack.android.settings.observeInt
import com.inkapplications.ack.android.symbol.SymbolFactory
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.coroutines.filterItemNotNull
import com.inkapplications.coroutines.mapItems
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
    private val aprs: PacketStorage,
    private val symbolFactory: SymbolFactory,
    private val logStateFactory: CombinedLogItemViewStateFactory,
    private val settings: SettingsReadAccess,
    private val mapSettings: MapSettings,
    private val localeSettings: LocaleSettings,
) {
    fun findMarkers(): Flow<Collection<MarkerViewState>> {
        return settings.observeInt(mapSettings.pinCount)
            .flatMapLatest { pinCount ->
                aprs.findRecent(pinCount)
                    .map { it.distinctBy { it.parsed.route.source } }
                    .mapItems { packet ->
                        when (val data = packet.parsed.data) {
                            is PacketData.Position -> MarkerViewState(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))
                            else -> null
                        }
                    }
                    .filterItemNotNull()
                    .onEach { logger.info("New set of ${it.size} map markers.") }
            }
    }

    fun findLogItem(id: Long): Flow<LogItemViewState?> {
        return settings.observeBoolean(localeSettings.preferMetric).flatMapLatest { metric ->
            aprs.findById(id).map { it?.let { logStateFactory.create(it.id, it.parsed, metric) } }
        }
    }
}

