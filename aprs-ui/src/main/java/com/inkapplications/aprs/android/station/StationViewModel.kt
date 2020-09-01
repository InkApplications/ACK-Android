package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.karps.structures.unit.Coordinates

data class StationViewModel(
    val markers: List<MarkerViewModel>,
    val center: Coordinates,
    val zoom: Double,
    val name: String,
    val comment: String
) {
    val mapVisible = markers.isNotEmpty()
}
