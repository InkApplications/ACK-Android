package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.settings.license.LicensePromptFieldValues
import com.inkapplications.ack.structures.station.StationAddress
import dagger.Reusable
import javax.inject.Inject

/**
 * Create onboarding state objects from the current data.
 */
@Reusable
class OnboardingStateFactory @Inject constructor() {
    fun screenState(data: OnboardingData): OnboardingState {
        return when {
            data.agreementRevision != data.latestRevision -> OnboardingState.UserAgreement
            !data.completedLicense -> OnboardingState.LicensePrompt(
                initialValues = LicensePromptFieldValues(
                    callsign = data.currentAddress?.toString().orEmpty(),
                    passcode = data.currentPasscode?.toString().orEmpty(),
                )
            )
            else -> OnboardingState.Complete
        }
    }

    /**
     * Data required to render the onboarding screens.
     */
    data class OnboardingData(
        val latestRevision: Int,
        val agreementRevision: Int? = null,
        val completedLicense: Boolean = false,
        val currentAddress: StationAddress? = null,
        val currentPasscode: Int? = null,
    )
}
