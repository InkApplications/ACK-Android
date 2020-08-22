package com.inkapplications.aprs.android.map

import android.content.res.Resources
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.settings.IntSetting
import com.inkapplications.aprs.android.settings.Setting
import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Reusable
import javax.inject.Inject

@Reusable
class MapSettings @Inject constructor(
    private val resources: Resources
): SettingsProvider {
    val pinCount = IntSetting(
        key = "map.pins",
        name = resources.getString(R.string.map_settings_pins_title),
        categoryName = resources.getString(R.string.map_settings_category)
    )

    override val settings: List<Setting> = listOf(pinCount)
}