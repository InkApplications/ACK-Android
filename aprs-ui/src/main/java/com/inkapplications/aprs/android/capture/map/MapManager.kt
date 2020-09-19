package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.map.Map
import dagger.Reusable
import javax.inject.Inject

/**
 * Manage services for the map screen.
 */
@Reusable
class MapManager @Inject constructor(
    private val mapData: MapDataRepository,
    private val mapState: MapState
) {
    /**
     * Enable the "show my position" function on the map.
     */
    fun enablePositionTracking() {
        mapState.trackMyPosition.value = true
    }

    /**
     * Disable the "show my position" function on the map.
     */
    fun disablePositionTracking() {
        mapState.trackMyPosition.value = false
    }

    /**
     * Factory to create Map's events access class at runtime.
     *
     * Since the mapbox map isn't available until the view is created,
     * this can create an injected events class with runtime dependencies.
     */
    fun createEventsAccess(map: Map): MapEvents = MapEvents(mapData, mapState, map)
}
