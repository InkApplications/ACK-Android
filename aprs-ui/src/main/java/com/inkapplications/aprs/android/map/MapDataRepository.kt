package com.inkapplications.aprs.android.map

import com.google.android.libraries.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.AprsPacket
import com.inkapplications.aprs.data.Position
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
    fun findMarkers(limit: Int = 100): Flow<Collection<MarkerOptions>> {
        return aprs.findRecent(limit)
            .map { it.filterIsInstance<AprsPacket.Location>() }
            .map { it.distinctBy { it.source } }
            .mapEach { packet ->
                MarkerOptions().apply {
                    position(packet.position.toLatLng())
                    title(packet.comment)
                    icon(packet.symbol.toBitmapDescriptor())
                }
            }
            .onEach { logger.info("New set of ${it.size} map markers.") }
    }

    private fun Pair<Char, Char>.toBitmapDescriptor() = symbolFactory
        .createSymbol(this)
        .let(BitmapDescriptorFactory::fromBitmap)

    private fun Position.toLatLng() = LatLng(latitude, longitude)
}
