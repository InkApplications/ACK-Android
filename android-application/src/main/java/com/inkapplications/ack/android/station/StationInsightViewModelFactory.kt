package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.android.log.SummaryFactory
import com.inkapplications.ack.android.map.*
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.capabilities.Commented
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.capabilities.Report
import com.inkapplications.android.extensions.ViewModelFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Convert raw station data into data needed to render the insight view for a station.
 */
@Reusable
class StationInsightViewModelFactory @Inject constructor(
    private val markerViewModelFactory: ViewModelFactory<CapturedPacket, MarkerViewModel?>,
    private val summaryFactory: SummaryFactory,
): ViewModelFactory<StationData, InsightViewModel> {
    override fun create(data: StationData): InsightViewModel {
        val firstMapable = data.packets.firstOrNull { it.parsed.data is Mapable }
        val firstWeatherData = data.packets.firstNotNullOfOrNull { it.parsed.data as? PacketData.Weather }

        return InsightViewModel(
            name = data.packets.first().parsed.route.source.callsign.canonical,
            markers = firstMapable?.let { markerViewModelFactory.create(it) }
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
