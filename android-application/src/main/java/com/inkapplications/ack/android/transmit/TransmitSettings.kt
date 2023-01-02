package com.inkapplications.ack.android.transmit

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.ValidationResult
import com.inkapplications.ack.android.input.Validator
import com.inkapplications.ack.android.settings.*
import com.inkapplications.ack.android.settings.transformer.*
import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.station.StationAddress
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.WholePercentage
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.structure.roundToInt
import inkapplications.spondee.structure.value
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


class TransmitSettings @Inject constructor(
    resources: StringResources,
): SettingsProvider {
    @OptIn(ExperimentalTime::class)
    val minRate = IntBackedSetting(
        key = "transmit.rate.min",
        name = resources.getString(R.string.transmit_settings_rate_min),
        defaultData = 10.minutes,
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = MinuteTransformer,
    )

    @OptIn(ExperimentalTime::class)
    val maxRate = IntBackedSetting(
        key = "transmit.rate.max",
        name = resources.getString(R.string.transmit_settings_rate_max),
        defaultData = 5.minutes,
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = MinuteTransformer,
    )

    @OptIn(ExperimentalTime::class)
    val distance = IntBackedSetting(
        key = "transmit.distance",
        name = resources.getString(R.string.transmit_settings_distance),
        defaultData = 5.miles,
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = MileTransformer,
    )

    @OptIn(ExperimentalTime::class)
    val preamble = IntBackedSetting(
        key = "transmit.preamble",
        name = resources.getString(R.string.transmit_settings_preamble),
        defaultData = 1.seconds,
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = MillisecondTransformer,
    )

    val digipath = StringBackedSetting(
        key = "transmit.digipath",
        name = resources.getString(R.string.transmit_settings_digipath),
        defaultData = StationAddress("WIDE1", "1").let(::Digipeater).let(::listOf),
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = PathTransformer,
    )

    val symbol = StringBackedSetting(
        key = "transmit.symbol",
        name = resources.getString(R.string.transmit_settings_symbol),
        defaultData = Symbol.Primary('$'),
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = SymbolTransformer,
    )

    val destination = StringBackedSetting(
        key = "transmit.destination",
        name = resources.getString(R.string.transmit_settings_destination),
        defaultData = StationAddress("APZ022"),
        categoryName = resources.getString(R.string.transmit_settings_category),
        advanced = true,
        transformer = StationAddressTransformer,
    )

    val comment = StringSetting(
        key = "transmit.comment",
        name = resources.getString(R.string.transmit_settings_comment),
        defaultValue = "Hello!",
        categoryName = resources.getString(R.string.transmit_settings_category),
    )

    val volume = IntBackedSetting(
        key = "transmit.volume",
        name = resources.getString(R.string.transmit_settings_volume),
        defaultData = 50.percent,
        categoryName = resources.getString(R.string.transmit_settings_category),
        transformer = PercentageTransformer,
        dataValidator = object: Validator<Percentage> {
            override fun validate(input: Percentage): ValidationResult {
                return if (input.toWholePercentage().roundToInt() in (0..100)) {
                    ValidationResult.Valid
                } else {
                    ValidationResult.Error("Must be between 0 and 100")
                }
            }
        }
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
