package com.inkapplications.aprs.android.station

import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import inkapplications.spondee.spatial.*

data class StationViewModel(
    val name: String = "",
    val comment: String = "",
    val markers: List<MarkerViewModel> = emptyList(),
    val center: GeoCoordinates = GeoCoordinates(0.latitude, 0.longitude),
    val zoom: Double = ZoomLevels.MIN,
    val temperature: String = "",
    val wind: String = "",
    val altitude: String = ""
) {
    val mapVisible = markers.isNotEmpty()
    val temperatureVisible = temperature.isNotEmpty()
    val windVisible = wind.isNotEmpty()
    val altitudeVisible = altitude.isNotEmpty()
}
