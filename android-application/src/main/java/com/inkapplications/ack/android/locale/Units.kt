@file:OptIn(SimpleNumberFormats::class)

package com.inkapplications.ack.android.locale

import inkapplications.spondee.format.SimpleNumberFormats
import inkapplications.spondee.format.formatDecimal
import inkapplications.spondee.measure.*
import inkapplications.spondee.measure.metric.toCelsius
import inkapplications.spondee.measure.metric.toKilometersPerHourValue
import inkapplications.spondee.measure.metric.toMetersPerSecondValue
import inkapplications.spondee.measure.us.toFahrenheit
import inkapplications.spondee.measure.us.toFeet
import inkapplications.spondee.measure.us.toMiles
import inkapplications.spondee.measure.us.toMilesPerHourValue
import inkapplications.spondee.structure.*

fun Length.format(metric: Boolean) = when {
    metric && toMeters() > 1000.0 -> toMeters().format(Kilo)
    metric -> toMeters().format()
    toFeet() > 1000 -> toMiles().format()
    else -> toFeet().format()
}

fun Speed.format(metric: Boolean) = when {
    metric && toMetersPerSecondValue() >= 1000 -> "${toKilometersPerHourValue().formatDecimal()}km/h"
    metric -> "${toMetersPerSecondValue().formatDecimal()}m/s"
    else -> "${toMilesPerHourValue().formatDecimal()}mph"
}

fun Temperature.format(metric: Boolean) = when {
    metric -> toCelsius().format(decimals = 1)
    else -> toFahrenheit().format()
}
