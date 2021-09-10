package com.inkapplications.aprs.android.onboard

data class OnboardingState(
    val agreementRequired: Boolean = true,
    val licensePromptRequired: Boolean = true,
    val callsignError: String? = null,
    val passcodeError: String? = null,
) {
    val finished = !agreementRequired && !licensePromptRequired
}
