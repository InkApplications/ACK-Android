package com.inkapplications.aprs.android.onboard

data class OnboardingState(
    val agreementRequired: Boolean = true,
    val licensePromptRequired: Boolean = true,
) {
    val finished = !agreementRequired && !licensePromptRequired
}

data class LicensePromptFieldValues(
    val callsign: String,
    val passcode: String,
)
