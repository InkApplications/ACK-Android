package com.inkapplications.aprs.android.station

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.locale.getLocalizedShortDistance
import com.inkapplications.aprs.android.locale.getLocalizedSpeed
import com.inkapplications.aprs.android.map.MarkerViewModel
import com.inkapplications.aprs.android.map.ZoomLevels
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.symbolOf
import com.inkapplications.karps.structures.unit.Coordinates
import com.inkapplications.karps.structures.unit.Distance
import com.inkapplications.karps.structures.unit.Latitude
import com.inkapplications.karps.structures.unit.Longitude
import dagger.Reusable
import javax.inject.Inject

@Reusable
class StationViewModelFactory @Inject constructor(
    private val symbolFactory: SymbolFactory,
    private val stringResources: StringResources
) {
    private val defaultWeatherSymbol = symbolOf('/', 'W')

    fun create(
        packet: CapturedPacket?,
        metric: Boolean
    ) = when (val data = packet?.data) {
        is AprsPacket.Position -> StationViewModel(
            markers = listOf(MarkerViewModel(packet.id, data.coordinates, symbolFactory.createSymbol(data.symbol))),
            center = data.coordinates,
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.comment,
            altitude = data.altitude.distanceString(metric)
        )
        is AprsPacket.Weather -> StationViewModel(
            markers = data.position?.let {
                listOf(MarkerViewModel(
                    id = packet.id,
                    coordinates = it,
                    symbol = symbolFactory.createSymbol(data.symbol ?: defaultWeatherSymbol)
                ))
            } ?: emptyList(),
            temperature = data.temperature?.toString().orEmpty(),
            wind = data.windString(metric),
            center = data.position ?: Coordinates(Latitude(0.0), Longitude(0.0)),
            zoom = ZoomLevels.ROADS,
            name = data.source.toString(),
            comment = data.body,
            altitude = data.altitude.distanceString(metric)

        )
        else -> StationViewModel(
            name = data?.source?.toString().orEmpty(),
            comment = data?.body.orEmpty()
        )
    }

    private fun Distance?.distanceString(metric: Boolean) = this
            ?.let { stringResources.getLocalizedShortDistance(it, metric) }
            .orEmpty()

    private fun AprsPacket.Weather.windString(metricUnits: Boolean): String {
        val direction = windData.direction
        val speed = windData.speed?.let { stringResources.getLocalizedSpeed(it, metricUnits) }
        val gust = windData.gust?.let { stringResources.getLocalizedSpeed(it, metricUnits) }

        return when {
            direction != null && speed != null && gust != null -> stringResources.getString(R.string.station_wind_format_full, direction, speed, gust)
            direction != null && speed != null -> stringResources.getString(R.string.station_wind_format_wind_only, direction, speed)
            direction != null -> stringResources.getString(R.string.station_wind_format_direction_only, direction)
            speed != null -> stringResources.getString(R.string.station_wind_format_speed_only, speed)
            else -> ""
        }
    }
}
