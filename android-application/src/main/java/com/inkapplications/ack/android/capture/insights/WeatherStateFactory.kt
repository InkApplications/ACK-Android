package com.inkapplications.ack.android.capture.insights

import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.android.extensions.format.DateTimeFormatter
import dagger.Reusable
import inkapplications.spondee.measure.us.toFahrenheit
import inkapplications.spondee.measure.us.toInches
import inkapplications.spondee.measure.us.toMilesPerHourValue
import inkapplications.spondee.structure.toFloat
import inkapplications.spondee.structure.toInt
import javax.inject.Inject

/**
 * Generate weather view state models based on the most relevant weather packet.
 */
@Reusable
class WeatherStateFactory @Inject constructor(
    private val dateTimeFormatter: DateTimeFormatter,
) {
    /**
     * Generate a view state for weather conditions.
     *
     * @param weatherPacket The most relevant weather packet seen by the user
     * @param metric Whether to generate the view data in metric dimensions
     */
    fun createState(weatherPacket: CapturedPacket?, metric: Boolean): InsightsWeatherState {
        return when (val weatherData = weatherPacket?.parsed?.data as? PacketData.Weather) {
            null -> InsightsWeatherState.Unknown
            else -> InsightsWeatherState.DisplayRecent(
                temperature = weatherData.temperature?.format(metric) ?: "--",
                weatherReporter = weatherPacket.parsed.route.source.toString(),
                weatherReportTime = weatherPacket.received.let { dateTimeFormatter.formatTimestamp(it) },
                icon = when {
                    weatherData.precipitation.snowLast24Hours?.toInches()?.toFloat() ?: 0f > 0 -> InsightsWeatherState.WeatherIcon.Snow
                    weatherData.precipitation.rainLastHour?.toInches()?.toFloat() ?: 0f > 0 -> InsightsWeatherState.WeatherIcon.Rain
                    weatherData.windData.speed?.toMilesPerHourValue()?.toInt() ?: 0 > 20 -> InsightsWeatherState.WeatherIcon.Windy
                    weatherData.windData.gust?.toMilesPerHourValue()?.toInt() ?: 0 > 20 -> InsightsWeatherState.WeatherIcon.Windy
                    weatherData.humidity?.toDecimal()?.toFloat() ?: 0f > .75f
                        && weatherData.temperature?.toFahrenheit()?.toInt() ?: 0 > 65 -> InsightsWeatherState.WeatherIcon.Humid
                    else -> InsightsWeatherState.WeatherIcon.Normal
                }
            )
        }
    }
}
