package com.inkapplications.aprs.android.capture.map

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.settings.IntSetting
import com.inkapplications.aprs.android.settings.Setting
import com.inkapplications.aprs.android.settings.SettingsProvider
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
        defaultValue = 500
    )

    override val settings: List<Setting> = listOf(pinCount)
}
