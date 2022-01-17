package com.inkapplications.android.extensions.location

import inkapplications.spondee.measure.Length
import inkapplications.spondee.spatial.GeoCoordinates

data class LocationUpdate(
    val location: GeoCoordinates,
    val altitude: Length,
)
