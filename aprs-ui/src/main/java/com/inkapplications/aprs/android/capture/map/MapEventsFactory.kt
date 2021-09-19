package com.inkapplications.aprs.android.capture.map

import com.inkapplications.aprs.android.connection.ConnectionSettings
import com.inkapplications.aprs.android.map.Map
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import dagger.Reusable
import javax.inject.Inject

/**
 * Creates map events access at runtime.
 */
@Reusable
class MapEventsFactory @Inject constructor(
    private val mapData: MapDataRepository,
) {
    /**
     * Factory to create Map's events access class at runtime.
     *
     * Since the mapbox map isn't available until the view is created,
     * this can create an injected events class with runtime dependencies.
     */
    fun createEventsAccess(map: Map): MapEvents = MapEvents(mapData, map)
}
