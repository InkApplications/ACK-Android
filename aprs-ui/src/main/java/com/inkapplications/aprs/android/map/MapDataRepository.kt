package com.inkapplications.aprs.android.map

import android.graphics.Bitmap
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Symbol
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.kotlin.mapEach
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import dagger.Reusable
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
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
            .map { it.filterIsInstance<AprsPacket.Position>() }
            .map { it.distinctBy { it.source } }
            .mapEach { packet ->
                MarkerViewModel(packet.coordinates, packet.comment, symbolFactory.createSymbol(packet.symbol))
            }
            .onEach { logger.info("New set of ${it.size} map markers.") }
    }
}

data class MarkerViewModel(
    val coordinates: Coordinates,
    val popupText: String,
    val symbol: Bitmap
)

@Reusable
class MapManagerFactory @Inject constructor() {
    fun create(view: MapView, map: MapboxMap, style: Style): MapManager {
        return MapManager(view, map, style)
    }
}

class MapManager(
    private val view: MapView,
    private val map: MapboxMap,
    private val style: Style
) {
    private val symbolManager = SymbolManager(view, map, style).also {
        it.iconAllowOverlap = true
    }

    fun showMarkers(markers: Collection<MarkerViewModel>) {
        markers
            .map { marker ->
                SymbolOptions()
                    .withLatLng(LatLng(marker.coordinates.latitude.decimal, marker.coordinates.longitude.decimal))
                    .withIconImage(createImage(marker.symbol, style))
            }
            .run { symbolManager.create(this) }
    }

    private fun createImage(image: Bitmap, style: Style): String {
        val id = UUID.randomUUID().toString()

        style.addImage(id, image)

        return id
    }
}
