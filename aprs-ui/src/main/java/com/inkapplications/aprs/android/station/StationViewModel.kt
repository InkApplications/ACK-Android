package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude

data class StationViewModel(
    val name: String,
    val comment: String,
    val markers: List<MarkerViewModel> = emptyList(),
    val center: Coordinates = Coordinates(Latitude(0.0), Longitude(0.0)),
    val zoom: Double = ZoomLevels.MIN,
    val weather: String = ""
) {
    val mapVisible = markers.isNotEmpty()
    val weatherVisible = weather.isNotEmpty()
}
