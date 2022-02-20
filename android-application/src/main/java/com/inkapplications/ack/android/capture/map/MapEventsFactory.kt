package com.inkapplications.ack.android.capture.map

import com.inkapplications.ack.android.map.Map
import com.inkapplications.android.extensions.location.LocationAccess
import dagger.Reusable
import javax.inject.Inject

/**
 * Creates map events access at runtime.
 */
@Reusable
class MapEventsFactory @Inject constructor(
    private val mapData: MapDataRepository,
    private val location: LocationAccess,
) {
    /**
     * Factory to create Map's events access class at runtime.
     *
     * Since the mapbox map isn't available until the view is created,
     * this can create an injected events class with runtime dependencies.
     */
    fun createEventsAccess(map: Map): MapEvents = MapEvents(mapData, location, map)
}
