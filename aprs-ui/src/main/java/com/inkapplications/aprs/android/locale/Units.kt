package com.inkapplications.aprs.android.locale

import inkapplications.spondee.format.SimpleNumberFormats
import inkapplications.spondee.format.formatDecimal
import inkapplications.spondee.measure.*
import inkapplications.spondee.structure.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

fun Length.format(metric: Boolean) = when {
    metric && value(Meters) > 1000 -> "${Meters.format(this, Kilo)}"
    metric -> Meters.format(this)
    !metric && value(Feet) > 1000 -> Miles.format(this)
    else -> Feet.format(this)
}

fun Speed.format(metric: Boolean) = when {
    metric && value(MetersPerSecond) >= 1000 -> KilometersPerHour.format(this)
    metric -> MetersPerSecond.format(this)
    else -> MilesPerHour.format(this)
}

fun Temperature.format(metric: Boolean) = when {
    metric -> Celsius.format(this, decimals = 1)
    else -> Fahrenheit.format(this, decimals = 0)
}

@OptIn(ExperimentalTime::class, SimpleNumberFormats::class)
private object KilometersPerHour: DoubleUnit<Speed>, Symbolized, UnitFormatter<Speed> {
    override val symbol: String = "km/h"
    override fun convertValue(value: Speed): Double {
        return value.lengthComponent.value(Kilo, Meters) / value.durationComponent.toDouble(DurationUnit.HOURS)
    }

    override fun of(value: Number): Speed {
        return MetersPerSecond.of(Meters.of(Kilo, value).value(Meters) / Duration.hours(1).toDouble(DurationUnit.SECONDS))
    }

    override fun format(measurement: Speed, decimals: Int, decimalSeparator: Char): String = "${MilesPerHour.convertValue(measurement).formatDecimal(decimals, true, decimalSeparator)}${MilesPerHour.symbol}"
    override fun format(measurement: Speed, siScale: SiScale, decimals: Int, decimalSeparator: Char): String = "${measurement.value(siScale, this).formatDecimal(decimals, true, decimalSeparator)}${siScale.symbol}${MilesPerHour.symbol}"
}
