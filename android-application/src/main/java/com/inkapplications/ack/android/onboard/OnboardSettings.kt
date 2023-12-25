package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.settings.*
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import javax.inject.Inject

@Reusable
class OnboardSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val agreementRevision = IntSetting(
        key = "onboarding.agreement",
        name = resources.getString(R.string.settings_onboard_license),
        categoryName = resources.getString(R.string.settings_onboard_category),
        defaultValue = 0,
        visibility = SettingVisibility.Dev,
    )
    val completedLicensePrompt = BooleanSetting(
        key = "onboarding.license",
        name = resources.getString(R.string.settings_onboard_license),
        categoryName = resources.getString(R.string.settings_onboard_category),
        defaultValue = false,
        visibility = SettingVisibility.Dev,
    )

    override val settings: List<Setting> = listOf(
        agreementRevision,
        completedLicensePrompt,
    )
}
