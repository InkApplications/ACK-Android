package com.inkapplications.aprs.android.locale

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import inkapplications.spondee.measure.*
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.value
import kotlin.math.roundToInt

/**
 * Get a formatted string for a speed unit, based on system of measurement.
 */
fun StringResources.getLocalizedSpeed(speed: Speed, metric: Boolean) = when {
    metric -> getString(R.string.locale_unit_speed_metric, speed.value(Kilo, MetersPerSecond).roundToInt() * 3600)
    else -> getString(R.string.locale_unit_speed, speed.value(MilesPerHour).roundToInt())
}

/**
 * Get a short-distance string.
 *
 * In this case "short" is referring to feet/meters as opposed to
 * miles/kilometers.
 */
fun StringResources.getLocalizedShortDistance(distance: Length, metric: Boolean) = when {
    metric -> getString(R.string.locale_unit_distance_short_metric, distance.value(Meters).roundToInt())
    else -> getString(R.string.locale_unit_distance_short, distance.value(Feet).roundToInt())
}
