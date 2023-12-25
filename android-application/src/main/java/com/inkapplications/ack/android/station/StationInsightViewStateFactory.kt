package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.map.CameraPositionDefaults
import com.inkapplications.ack.android.map.MapCameraPosition
import com.inkapplications.ack.android.map.MarkerViewState
import com.inkapplications.ack.android.map.ZoomLevels
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilities.Commented
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.capabilities.Report
import com.inkapplications.android.extensions.ViewStateFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Convert raw station data into data needed to render the insight view for a station.
 */
@Reusable
class StationInsightViewStateFactory @Inject constructor(
    private val markerViewStateFactory: ViewStateFactory<CapturedPacket, MarkerViewState?>,
    private val summaryFactory: SummaryFactory,
): ViewStateFactory<StationData, InsightViewState> {
    override fun create(data: StationData): InsightViewState {
        val firstMapable = data.packets.firstOrNull { it.parsed.data is Mapable }
        val firstWeatherData = data.packets.firstNotNullOfOrNull { it.parsed.data as? PacketData.Weather }

        return InsightViewState(
            name = data.packets.first().parsed.route.source.callsign.canonical,
            markers = firstMapable?.let { markerViewStateFactory.create(it) }
                ?.let { listOf(it) }
                .orEmpty(),
            mapCameraPosition = (firstMapable?.parsed?.data as? Mapable)?.coordinates
                ?.let { MapCameraPosition(it, ZoomLevels.ROADS) }
                ?: CameraPositionDefaults.unknownLocation,
            comment = data.packets.firstNotNullOfOrNull { (it.parsed.data as? Commented)?.comment },
            temperature = firstWeatherData?.temperature
                ?.format(data.metric),
            wind = firstWeatherData?.let { summaryFactory.createWindSummary(it.windData, data.metric) },
            altitude = data.packets.firstNotNullOfOrNull { it.parsed.data as? Report }
                ?.altitude
                ?.format(data.metric),
            telemetryValues = data.packets.firstNotNullOfOrNull { it.parsed.data as? PacketData.TelemetryReport }
                ?.data,
            telemetrySequence = data.packets.firstNotNullOfOrNull { it.parsed.data as? PacketData.TelemetryReport }
                ?.sequenceId,
        )
    }
}
