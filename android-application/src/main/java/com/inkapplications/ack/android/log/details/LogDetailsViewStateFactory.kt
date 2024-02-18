package com.inkapplications.ack.android.log.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.maps.*
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketOrigin
import com.inkapplications.ack.structures.PacketData.TelemetryReport
import com.inkapplications.ack.structures.PacketData.Weather
import com.inkapplications.ack.structures.capabilities.Commented
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.capabilities.Report
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.ViewStateFactory
import com.inkapplications.android.extensions.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Convert Log data into the model used to render the view for a specific packet.
 */
class LogDetailsViewStateFactory @Inject constructor(
    private val summaryFactory: SummaryFactory,
    private val markerViewStateFactory: ViewStateFactory<CapturedPacket, MarkerViewState?>,
    private val timeFormatter: DateTimeFormatter,
    private val stringResources: StringResources,
) {
    fun create(data: LogDetailData): LogDetailsState.Loaded {
        val packetData = data.packet.parsed.data
        return LogDetailsState.Loaded(
            callsign = data.packet.parsed.route.source.callsign,
            name = data.packet.parsed.route.source.toString(),
            timestamp = timeFormatter.formatTimestamp(data.packet.received)
                .let { stringResources.getString(R.string.log_details_received_format, it) },
            comment = (packetData as? Commented)?.comment,
            mapable = packetData is Mapable && packetData.coordinates != null,
            mapViewState = MapViewModel(
                markers = markerViewStateFactory.create(data.packet)?.let { listOf(it) }.orEmpty(),
                cameraPosition = (data.packet.parsed.data as? Mapable)?.coordinates
                    ?.let { MapCameraPosition(it, ZoomLevels.ROADS) }
                    ?: CameraPositionDefaults.unknownLocation,
            ),
            temperature = (packetData as? Weather)?.temperature?.format(data.metric),
            wind = (packetData as? Weather)?.let { summaryFactory.createWindSummary(it.windData, data.metric) },
            altitude = (packetData as? Report)?.altitude?.format(data.metric),
            rawSource = data.packet.raw.decodeToString().takeIf { data.debug },
            telemetryValues = (packetData as? TelemetryReport)?.data,
            telemetrySequence = (packetData as? TelemetryReport)?.sequenceId,
            receiveIcon = when (data.packet.origin) {
                PacketOrigin.AprsIs -> Icons.Default.Cloud
                PacketOrigin.Ax25 -> Icons.Default.SettingsInputAntenna
                PacketOrigin.Tnc -> Icons.Default.Bluetooth
                PacketOrigin.Local -> Icons.Default.Storage
            },
            receiveIconDescription = when (data.packet.origin) {
                PacketOrigin.Ax25 -> stringResources.getString(R.string.log_icon_ax25)
                PacketOrigin.AprsIs -> stringResources.getString(R.string.log_icon_aprs_is)
                PacketOrigin.Tnc -> stringResources.getString(R.string.log_icon_tnc)
                PacketOrigin.Local -> stringResources.getString(R.string.log_icon_local)
            },
        )
    }
}

