package com.inkapplications.ack.android.log

import com.inkapplications.ack.android.R
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.Setting
import com.inkapplications.ack.android.settings.SettingsProvider
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LogSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val filterUnknown = BooleanSetting(
        key = "log.filter.unknown",
        name = resources.getString(R.string.log_setting_filter_unknown),
        categoryName = resources.getString(R.string.log_setting_category),
        defaultValue = false,
    )

    override val settings: List<Setting> = listOf(filterUnknown)
}
