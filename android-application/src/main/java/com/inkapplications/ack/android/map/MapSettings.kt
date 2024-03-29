package com.inkapplications.ack.android.map

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.IntegerValidator
import com.inkapplications.ack.android.settings.IntSetting
import com.inkapplications.ack.android.settings.Setting
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import javax.inject.Inject

@Reusable
class MapSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val pinCount = IntSetting(
        key = "map.pins",
        name = resources.getString(R.string.map_settings_pins_title),
        categoryName = resources.getString(R.string.map_settings_category),
        defaultValue = 500,
        validator = IntegerValidator(
            error = resources.getString(R.string.input_validator_positive_integer_error),
        )
    )

    override val settings: List<Setting> = listOf(pinCount)
}
