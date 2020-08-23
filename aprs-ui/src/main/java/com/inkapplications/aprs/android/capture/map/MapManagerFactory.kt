package com.inkapplications.aprs.android.capture.map

import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import dagger.Reusable
import javax.inject.Inject

/**
 * Create Map Managers at runtime.
 *
 * Since the mapbox map isn't available until the view is created,
 * this can create an injected manager with runtime dependencies.
 */
@Reusable
class MapManagerFactory @Inject constructor(
    private val mapData: MapDataRepository
) {
    fun create(view: MapView, mapbox: MapboxMap, style: Style): MapManager {
        val map = Map(view, mapbox, style)

        return MapManager(mapData, map)
    }
}
