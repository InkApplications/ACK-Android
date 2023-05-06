package com.inkapplications.ack.android.log.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.SettingsInputAntenna
import androidx.compose.material.icons.filled.Storage
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.structures.PacketData.TelemetryReport
import com.inkapplications.ack.structures.PacketData.Weather
import com.inkapplications.ack.structures.capabilities.Commented
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.capabilities.Report
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Convert Log data into the model used to render the view for a specific packet.
 */
class LogDetailsViewStateFactory @Inject constructor(
    private val summaryFactory: SummaryFactory,
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
            temperature = (packetData as? Weather)?.temperature?.format(data.metric),
            wind = (packetData as? Weather)?.let { summaryFactory.createWindSummary(it.windData, data.metric) },
            altitude = (packetData as? Report)?.altitude?.format(data.metric),
            rawSource = data.packet.raw.decodeToString().takeIf { data.debug },
            telemetryValues = (packetData as? TelemetryReport)?.data,
            telemetrySequence = (packetData as? TelemetryReport)?.sequenceId,
            receiveIcon = when (data.packet.source) {
                PacketSource.AprsIs -> Icons.Default.Cloud
                PacketSource.Ax25 -> Icons.Default.SettingsInputAntenna
                PacketSource.Local -> Icons.Default.Storage
            },
            receiveIconDescription = when (data.packet.source) {
                PacketSource.Ax25 -> stringResources.getString(R.string.log_icon_ax25)
                PacketSource.AprsIs -> stringResources.getString(R.string.log_icon_aprs_is)
                PacketSource.Local -> stringResources.getString(R.string.log_icon_local)
            },
        )
    }
}

