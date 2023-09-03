package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.IntegerValidator
import com.inkapplications.ack.android.input.MaxLengthValidator
import com.inkapplications.ack.android.input.MinLengthValidator
import com.inkapplications.ack.android.input.ValidationResult
import com.inkapplications.ack.android.input.Validator
import com.inkapplications.ack.android.input.plus
import com.inkapplications.ack.android.settings.IntBackedSetting
import com.inkapplications.ack.android.settings.Setting
import com.inkapplications.ack.android.settings.SettingVisibility
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.android.settings.StringBackedSetting
import com.inkapplications.ack.android.settings.StringSetting
import com.inkapplications.ack.android.settings.transformer.MileTransformer
import com.inkapplications.ack.android.settings.transformer.MillisecondTransformer
import com.inkapplications.ack.android.settings.transformer.MinuteTransformer
import com.inkapplications.ack.android.settings.transformer.PercentageTransformer
import com.inkapplications.ack.android.settings.transformer.StationAddressTransformer
import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.android.extensions.StringResources
import inkapplications.spondee.measure.us.miles
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.percent
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.structure.roundToInt
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class TransmitSettings @Inject constructor(
    resources: StringResources,
): SettingsProvider {
    val minRate = IntBackedSetting(
        key = "transmit.rate.min",
        name = resources.getString(R.string.transmit_settings_rate_min),
        defaultData = 10.minutes,
        categoryName = resources.getString(R.string.transmit_settings_category),
        storageTransformer = MinuteTransformer,
        inputValidator = IntegerValidator(
            error = resources.getString(R.string.input_validator_positive_integer_error),
            zeroInclusive = false,
        ),
    )

    val maxRate = IntBackedSetting(
        key = "transmit.rate.max",
        name = resources.getString(R.string.transmit_settings_rate_max),
        defaultData = 5.minutes,
        categoryName = resources.getString(R.string.transmit_settings_category),
        storageTransformer = MinuteTransformer,
        inputValidator = IntegerValidator(
            error = resources.getString(R.string.input_validator_positive_integer_error),
            zeroInclusive = false,
        ),
    )

    val distance = IntBackedSetting(
        key = "transmit.distance",
        name = resources.getString(R.string.transmit_settings_distance),
        defaultData = 5.miles,
        categoryName = resources.getString(R.string.transmit_settings_category),
        storageTransformer = MileTransformer,
        inputValidator = IntegerValidator(
            error = resources.getString(R.string.input_validator_positive_integer_error),
            zeroInclusive = false,
        ),
    )

    val preamble = IntBackedSetting(
        key = "transmit.preamble",
        name = resources.getString(R.string.transmit_settings_preamble),
        defaultData = 1.seconds,
        categoryName = resources.getString(R.string.transmit_settings_category),
        visibility = SettingVisibility.Advanced,
        storageTransformer = MillisecondTransformer,
        inputValidator = IntegerValidator(
            error = resources.getString(R.string.input_validator_positive_integer_error),
            zeroInclusive = false,
        ),
    )

    val digipath = StringBackedSetting(
        key = "transmit.digipath",
        name = resources.getString(R.string.transmit_settings_digipath),
        defaultData = StationAddress("WIDE1", "1").let(::Digipeater).let(::listOf),
        visibility = SettingVisibility.Advanced,
        categoryName = resources.getString(R.string.transmit_settings_category),
        storageTransformer = PathTransformer,
    )

    val symbol = StringBackedSetting(
        key = "transmit.symbol.normalized",
        name = resources.getString(R.string.transmit_settings_symbol),
        defaultData = Symbol.Primary('$'),
        categoryName = resources.getString(R.string.transmit_settings_category),
        storageTransformer = SymbolTransformer,
        inputValidator = MinLengthValidator(
            minLength = 2,
            error = resources.getString(R.string.transmit_settings_symbol_length),
        ) + MaxLengthValidator(
            maxLength = 2,
            error = resources.getString(R.string.transmit_settings_symbol_length),
        ),
    )

    val destination = StringBackedSetting(
        key = "transmit.destination",
        name = resources.getString(R.string.transmit_settings_destination),
        defaultData = StationAddress("APZ022"),
        categoryName = resources.getString(R.string.transmit_settings_category),
        visibility = SettingVisibility.Advanced,
        storageTransformer = StationAddressTransformer,
    )

    val comment = StringSetting(
        key = "transmit.comment",
        name = resources.getString(R.string.transmit_settings_comment),
        defaultValue = "Hello!",
        categoryName = resources.getString(R.string.transmit_settings_category),
        validator = MaxLengthValidator(
            maxLength = 43,
            error = resources.getString(R.string.transmit_settings_comment_length),
        ),
    )

    val volume = IntBackedSetting(
        key = "transmit.volume",
        name = resources.getString(R.string.transmit_settings_volume),
        defaultData = 50.percent,
        categoryName = resources.getString(R.string.transmit_settings_category),
        storageTransformer = PercentageTransformer,
        dataValidator = object: Validator<Percentage> {
            override fun validate(input: Percentage): ValidationResult {
                return if (input.toWholePercentage().roundToInt() in (0..100)) {
                    ValidationResult.Valid
                } else {
                    ValidationResult.Error(
                        message = resources.getString(R.string.transmit_settings_volume_range_error),
                    )
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
