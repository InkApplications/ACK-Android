package com.inkapplications.aprs.android.capture.log

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.Setting
import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val filterUnknown = BooleanSetting(
        key = "log.filter.unknown",
        name = "Filter Out Unknown Packets",
        categoryName = "Log",
        defaultValue = false,
    )

    override val settings: List<Setting> = listOf(filterUnknown)
}
