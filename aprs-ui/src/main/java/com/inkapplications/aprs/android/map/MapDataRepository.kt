package com.inkapplications.aprs.android.map

import com.inkapplications.aprs.android.log.LogItem
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.kotlin.filterEachNotNull
import com.inkapplications.kotlin.mapEach
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Aggregate data needed to display the map.
 */
@Reusable
class MapDataRepository @Inject constructor(
    private val logger: KimchiLogger,
    private var aprs: AprsAccess,
    private var symbolFactory: SymbolFactory
) {
    fun findMarkers(limit: Int = 100): Flow<Collection<MarkerViewModel>> {
        return aprs.findRecent(limit)
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

    fun findLogItem(id: Long): Flow<LogItem?> {
        return aprs.findById(id).map { it?.let { LogItem(it.data, symbolFactory) } }
    }
}
