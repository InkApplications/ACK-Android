package com.inkapplications.ack.android.mapbox

import com.inkapplications.ack.android.maps.ZoomLevels

/**
 * Mapbox adaptation of zoom levels.
 */
internal val ZoomLevels.mapboxValue: Double get() = when (this) {
    ZoomLevels.MIN -> 0.0
    ZoomLevels.CONTINENT -> 2.0
    ZoomLevels.ISLANDS -> 4.0
    ZoomLevels.RIVERS -> 7.0
    ZoomLevels.ROADS -> 10.0
    ZoomLevels.BUILDINGS -> 15.0
    ZoomLevels.MAX -> 22.0
}
