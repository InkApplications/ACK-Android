package com.inkapplications.aprs.android.locale

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.Setting
import com.inkapplications.aprs.android.settings.SettingsProvider
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LocaleSettings @Inject constructor(
    stringResources: StringResources
): SettingsProvider {
    val preferMetric = BooleanSetting(
        key = "locale.metric",
        name = stringResources.getString(R.string.locale_setting_metric_name),
        categoryName = stringResources.getString(R.string.locale_setting_category),
        defaultValue = false
    )


    override val settings: List<Setting> = listOf(preferMetric)
}
