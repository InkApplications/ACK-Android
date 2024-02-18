package com.inkapplications.ack.android.log.details

import androidx.compose.ui.graphics.vector.ImageVector
import com.inkapplications.ack.android.maps.MapViewModel
import com.inkapplications.ack.structures.TelemetryValues
import com.inkapplications.ack.structures.station.Callsign

/**
 * Possible states for the Log Details Screen.
 */
sealed interface LogDetailsState {
    /**
     * Indicates that no data has been loaded yet.
     */
    object Initial: LogDetailsState

    /**
     * Log data for the packet.
     */
    data class Loaded(
        val callsign: Callsign,
        val name: String,
        val receiveIcon: ImageVector,
        val receiveIconDescription: String,
        val timestamp: String,
        val mapable: Boolean,
        val mapViewState: MapViewModel,
        val comment: String? = null,
        val temperature: String? = null,
        val wind: String? = null,
        val altitude: String? = null,
        val rawSource: String? = null,
        val telemetryValues: TelemetryValues? = null,
        val telemetrySequence: String? = null,
    ): LogDetailsState
}
