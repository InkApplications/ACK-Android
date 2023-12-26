package com.inkapplications.ack.android.map

import android.graphics.Bitmap
import com.inkapplications.ack.data.CaptureId
import inkapplications.spondee.spatial.GeoCoordinates

/**
 * View data for a marker displayed on the map.
 */
data class MarkerViewState(
    val id: CaptureId,
    val coordinates: GeoCoordinates,
    val symbol: Bitmap?
)
