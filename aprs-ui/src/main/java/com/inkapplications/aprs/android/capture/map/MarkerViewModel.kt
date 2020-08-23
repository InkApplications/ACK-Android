package com.inkapplications.aprs.android.capture.map

import android.graphics.Bitmap
import com.inkapplications.karps.structures.unit.Coordinates

/**
 * View data for a marker displayed on the map.
 */
data class MarkerViewModel(
    val id: Long,
    val coordinates: Coordinates,
    val symbol: Bitmap
)