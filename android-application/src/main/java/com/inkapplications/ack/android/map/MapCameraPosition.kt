package com.inkapplications.ack.android.map

import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude

/**
 * Contains information on how to display the map to the user
 */
data class MapCameraPosition(
    val coordinates: GeoCoordinates,
    val zoom: ZoomLevels,
)

object CameraPositionDefaults {
    val unknownLocation = MapCameraPosition(
        coordinates = GeoCoordinates(39.828497055897344.latitude, (-98.57943535336678).longitude),
        zoom = ZoomLevels.CONTINENT,
    )
}
