package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.settings.*
import dagger.Reusable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class OnboardingStateAccess @Inject constructor(
    readSettings: SettingsReadAccess,
    private val writeSettings: SettingsWriteAccess,
    private val onboardingSettings: OnboardSettings,
) {
    val screenState = readSettings.observeIntState(onboardingSettings.agreementRevision)
        .map {
            OnboardingState(agreementRequired = it != AGREEMENT_REVISION)
        }
        .combine(readSettings.observeBoolean(onboardingSettings.completedLicensePrompt)) { state, completedLicense ->
            state.copy(licensePromptRequired = !completedLicense)
        }

    fun setUserAgreed() {
        writeSettings.setInt(onboardingSettings.agreementRevision, AGREEMENT_REVISION)
    }

    fun setLicensePromptCompleted() {
        writeSettings.setBoolean(onboardingSettings.completedLicensePrompt, true)
    }
}
