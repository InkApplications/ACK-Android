package com.inkapplications.ack.android.locale

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.Setting
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.android.extensions.StringResources
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
