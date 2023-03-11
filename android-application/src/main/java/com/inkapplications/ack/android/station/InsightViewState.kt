package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.MarkerViewState
import com.inkapplications.ack.structures.TelemetryValues

/**
 * Summary data for a particular station to display to the user
 */
data class InsightViewState(
    val name: String = "",
    val markers: List<MarkerViewState> = emptyList(),
    val mapCameraPosition: MapCameraPosition = CameraPositionDefaults.unknownLocation,
    val comment: String? = null,
    val temperature: String? = null,
    val wind: String? = null,
    val altitude: String? = null,
    val telemetryValues: TelemetryValues? = null,
    val telemetrySequence: String? = null,
)
