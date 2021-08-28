package com.inkapplications.aprs.android.station

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.locale.format
import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.symbolOf
import dagger.Reusable
import inkapplications.spondee.measure.Length
import inkapplications.spondee.spatial.Degrees
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import inkapplications.spondee.structure.value
import javax.inject.Inject
import kotlin.math.roundToInt

@Reusable
class StationViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory,
    private val stringResources: StringResources
) {
    private val defaultWeatherSymbol = symbolOf('/', 'W')

    fun create(
        packet: CapturedPacket?,
        metric: Boolean,
        showDebug: Boolean,
    ) = when (val data = packet?.data) {
        is AprsPacket.Position -> StationViewModel(
            markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
            center = data.coordinates,
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.comment,
            altitude = data.altitude.distanceString(metric),
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.Weather -> StationViewModel(
            markers = data.coordinates?.let {
                listOf(MarkerViewModel(
                    id = packet.id,
                    coordinates = it,
                    symbol = symbolFactory.createSymbol(data.symbol ?: defaultWeatherSymbol)
                ))
            } ?: emptyList(),
            temperature = data.temperature?.format(metric).orEmpty(),
            wind = data.windString(metric),
            center = data.coordinates ?: GeoCoordinates(0.latitude, 0.longitude),
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.ObjectReport -> StationViewModel(
            markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
            center = data.coordinates,
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.comment,
            altitude = data.altitude.distanceString(metric),
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.ItemReport -> StationViewModel(
            markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
            center = data.coordinates,
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.comment,
            altitude = data.altitude.distanceString(metric),
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.Message -> StationViewModel(
            name = data.source.toString(),
            comment = data.message,
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.Unknown -> StationViewModel(
            name = data.source.toString(),
            comment = "",
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.TelemetryReport -> StationViewModel(
            name = data.source.toString(),
            comment = data.comment,
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.StatusReport -> StationViewModel(
            name = data.source.toString(),
            comment = data.status,
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        is AprsPacket.CapabilityReport -> StationViewModel(
            name = data.source.toString(),
            rawPacket = data,
            debugDataVisible = showDebug,
        )
        null -> StationViewModel()
    }

    private fun Length?.distanceString(metric: Boolean) = this?.format(metric).orEmpty()

    private fun AprsPacket.Weather.windString(metricUnits: Boolean): String {
        val direction = windData.direction?.value(Degrees)?.roundToInt()?.let { "${it}º" }
        val speed = windData.speed?.format(metricUnits)
        val gust = windData.gust?.format(metricUnits)

        return when {
            direction != null && speed != null && gust != null -> stringResources.getString(R.string.station_wind_format_full, direction, speed, gust)
            direction != null && speed != null -> stringResources.getString(R.string.station_wind_format_wind_only, direction, speed)
            direction != null -> stringResources.getString(R.string.station_wind_format_direction_only, direction)
            speed != null -> stringResources.getString(R.string.station_wind_format_speed_only, speed)
            else -> ""
        }
    }
}
