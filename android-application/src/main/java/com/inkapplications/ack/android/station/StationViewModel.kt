package com.inkapplications.ack.android.station

import androidx.compose.ui.graphics.vector.ImageVector
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.MarkerViewModel
import com.inkapplications.ack.structures.TelemetryValues

data class StationViewModel(
    val name: String = "",
    val comment: String = "",
    val markers: List<MarkerViewModel> = emptyList(),
    val mapCameraPosition: MapCameraPosition = CameraPositionDefaults.unknownLocation,
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
