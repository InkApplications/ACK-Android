package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.map.Map
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
    fun create(map: Map): MapManager = MapManager(mapData, map)
}
