package com.inkapplications.aprs.android.transmit

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.settings.*
import inkapplications.spondee.measure.Miles
import inkapplications.spondee.structure.value
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class TransmitSettings @Inject constructor(
    resources: StringResources,
): SettingsProvider {
    @OptIn(ExperimentalTime::class)
    val minRate = IntSetting(
        key = "transmit.rate.min",
        name = resources.getString(R.string.transmit_settings_rate_min),
        defaultValue = Duration.minutes(10).inWholeMinutes.toInt(),
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    @OptIn(ExperimentalTime::class)
    val maxRate = IntSetting(
        key = "transmit.rate.max",
        name = resources.getString(R.string.transmit_settings_rate_max),
        defaultValue = Duration.minutes(5).inWholeMinutes.toInt(),
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    @OptIn(ExperimentalTime::class)
    val distance = IntSetting(
        key = "transmit.distance",
        name = resources.getString(R.string.transmit_settings_distance),
        defaultValue = 5,
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    @OptIn(ExperimentalTime::class)
    val preamble = IntSetting(
        key = "transmit.preamble",
        name = resources.getString(R.string.transmit_settings_preamble),
        defaultValue = Duration.seconds(1).inWholeMilliseconds.toInt(),
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    val digipath = StringSetting(
        key = "transmit.digipath",
        name = resources.getString(R.string.transmit_settings_digipath),
        defaultValue = "WIDE1-1",
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    val symbol = StringSetting(
        key = "transmit.symbol",
        name = resources.getString(R.string.transmit_settings_symbol),
        defaultValue = "/$",
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    val destination = StringSetting(
        key = "transmit.destination",
        name = resources.getString(R.string.transmit_settings_destination),
        defaultValue = "APZ022",
        categoryName = resources.getString(R.string.transmit_settings_category),
        advanced = true,
    )

    val comment = StringSetting(
        key = "transmit.comment",
        name = resources.getString(R.string.transmit_settings_comment),
        defaultValue = "Hello!",
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    val volume = IntSetting(
        key = "transmit.volume",
        name = resources.getString(R.string.transmit_settings_volume),
        defaultValue = 50,
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    override val settings: List<Setting> = listOf(
        minRate,
        maxRate,
        distance,
        preamble,
        digipath,
        symbol,
        destination,
        comment,
        volume,
    )
}
