package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.settings.license.LicensePromptFieldValues

/**
 * Possible states of the onboarding screen.
 */
sealed interface OnboardingState {
    /**
     * Initial state, before any data is loaded.
     */
    object Initial: OnboardingState

    /**
     * Screen prompting the user to agree to the usage agreement.
     */
    object UserAgreement: OnboardingState

    /**
     * Screen prompting the user to enter their callsign information.
     */
    class LicensePrompt(
        val initialValues: LicensePromptFieldValues,
    ): OnboardingState

    /**
     * Screen state after all data has been completed.
     */
    object Complete: OnboardingState
}
