package com.inkapplications.ack.android.map

import android.graphics.Bitmap
import inkapplications.spondee.spatial.GeoCoordinates

/**
 * View data for a marker displayed on the map.
 */
data class MarkerViewModel(
    val id: Long,
    val coordinates: GeoCoordinates,
    val symbol: Bitmap?
)
