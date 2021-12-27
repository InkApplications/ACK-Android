package com.inkapplications.aprs.android.station

import androidx.compose.ui.graphics.vector.ImageVector
import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.TelemetryValues
import inkapplications.spondee.spatial.*

data class StationViewModel(
    val name: String = "",
    val comment: String = "",
    val markers: List<MarkerViewModel> = emptyList(),
    val center: GeoCoordinates = GeoCoordinates(0.latitude, 0.longitude),
    val zoom: Double = ZoomLevels.MIN,
    val temperature: String = "",
    val wind: String = "",
    val altitude: String = "",
    val rawSource: String = "",
    val debugDataVisible: Boolean = false,
    val telemetryValues: TelemetryValues? = null,
    val telemetrySequence: String? = null,
    val receiveIcon: ImageVector? = null,
    val receiveIconDescription: String? = null,
) {
    val mapVisible = markers.isNotEmpty()
    val temperatureVisible = temperature.isNotEmpty()
    val windVisible = wind.isNotEmpty()
    val altitudeVisible = altitude.isNotEmpty()
    val commentVisible = comment.isNotBlank()
}
