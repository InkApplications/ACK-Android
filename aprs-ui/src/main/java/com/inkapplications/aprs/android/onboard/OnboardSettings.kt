package com.inkapplications.aprs.android.onboard

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.settings.*
import dagger.Reusable
import javax.inject.Inject

@Reusable
class OnboardSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val agreementRevision = IntSetting(
        key = "onboarding.agreement",
        name = "License Agreement Revision",
        categoryName = "Onboarding",
        defaultValue = 0,
        advanced = true,
    )
    val completedLicensePrompt = BooleanSetting(
        key = "onboarding.license",
        name = "License Prompt Completed",
        categoryName = "Onboarding",
        defaultValue = false,
        advanced = true,
    )
    val callsign = StringSetting(
        key = "onboarding.callsign",
        name = "Callsign",
        categoryName = "Onboarding",
        defaultValue = "",
    )
    val passcode = StringSetting(
        key = "onboarding.passcode",
        name = "APRS-IS Passcode",
        categoryName = "Onboarding",
        defaultValue = "",
    )

    override val settings: List<Setting> = listOf(
        agreementRevision,
        completedLicensePrompt,
        callsign,
        passcode,
    )
}
