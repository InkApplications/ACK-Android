package com.inkapplications.ack.android.log.details

import androidx.compose.ui.graphics.vector.ImageVector
import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.MarkerViewModel
import com.inkapplications.ack.structures.TelemetryValues

sealed interface LogDetailsState {
    object Initial: LogDetailsState
    data class LogDetailsViewModel(
        val name: String,
        val receiveIcon: ImageVector,
        val receiveIconDescription: String,
        val comment: String? = null,
        val markers: List<MarkerViewModel> = emptyList(),
        val mapCameraPosition: MapCameraPosition = CameraPositionDefaults.unknownLocation,
        val temperature: String? = null,
        val wind: String? = null,
        val altitude: String? = null,
        val rawSource: String? = null,
        val telemetryValues: TelemetryValues? = null,
        val telemetrySequence: String? = null,
    ): LogDetailsState
}
