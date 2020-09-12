package com.inkapplications.aprs.android.locale

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import com.inkapplications.karps.structures.unit.Speed

/**
 * Get a formatted string for a speed unit, based on system of measurement.
 */
fun StringResources.getLocalizedSpeed(speed: Speed, metric: Boolean) = when {
    metric -> getString(R.string.locale_unit_speed_metric, speed.kilometersPerHour)
    else -> getString(R.string.locale_unit_speed, speed.milesPerHour)
}
