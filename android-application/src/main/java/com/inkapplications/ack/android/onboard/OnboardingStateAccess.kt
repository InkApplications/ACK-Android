package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.settings.*
import com.inkapplications.android.extensions.IntegerResources
import dagger.Reusable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class OnboardingStateAccess @Inject constructor(
    readSettings: SettingsReadAccess,
    private val writeSettings: SettingsWriteAccess,
    private val onboardingSettings: OnboardSettings,
    integers: IntegerResources,
) {
    private val revision = integers.getInteger(R.integer.usage_revision)
    val screenState = readSettings.observeIntState(onboardingSettings.agreementRevision)
        .map {
            OnboardingState(agreementRequired = it != revision)
        }
        .combine(readSettings.observeBoolean(onboardingSettings.completedLicensePrompt)) { state, completedLicense ->
            state.copy(licensePromptRequired = !completedLicense)
        }

    fun setUserAgreed() {
        writeSettings.setInt(onboardingSettings.agreementRevision, revision)
    }

    fun setLicensePromptCompleted() {
        writeSettings.setBoolean(onboardingSettings.completedLicensePrompt, true)
    }
}
