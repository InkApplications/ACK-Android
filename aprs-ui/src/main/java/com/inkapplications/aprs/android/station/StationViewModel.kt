package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.map.MarkerViewModel

data class StationViewModel(
    val markers: List<MarkerViewModel>,
    val name: String,
    val comment: String
) {
    val mapVisible = markers.isNotEmpty()
}
