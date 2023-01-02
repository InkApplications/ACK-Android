package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.locale.format
import com.inkapplications.ack.structures.WindData
import com.inkapplications.android.extensions.StringResources
import inkapplications.spondee.spatial.toDegrees
import inkapplications.spondee.structure.format
import javax.inject.Inject

/**
 * Create readable sentence summaries from packet data.
 */
class SummaryFactory @Inject constructor(
    private val stringResources: StringResources,
) {
    /**
     * Create a readable sentence out of various wind data, if available.
     */
    fun createWindSummary(data: WindData, metric: Boolean): String? {
        val direction = data.direction?.toDegrees()?.format()
        val speed = data.speed?.format(metric)
        val gust = data.gust?.format(metric)

        return when {
            direction != null && speed != null && gust != null -> stringResources.getString(R.string.station_wind_format_full, direction, speed, gust)
            direction != null && speed != null -> stringResources.getString(R.string.station_wind_format_wind_only, direction, speed)
            direction != null -> stringResources.getString(R.string.station_wind_format_direction_only, direction)
            speed != null -> stringResources.getString(R.string.station_wind_format_speed_only, speed)
            else -> null
        }
    }
}
