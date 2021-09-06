package com.inkapplications.aprs.android.onboard

import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.SettingsWriteAccess
import com.inkapplications.aprs.android.settings.observeBoolean
import com.inkapplications.coroutines.combinePair
import dagger.Reusable
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class OnboardingStateAccess @Inject constructor(
    readSettings: SettingsReadAccess,
    private val writeSettings: SettingsWriteAccess,
    private val onboardingSettings: OnboardSettings,
) {
    val screenState = readSettings.observeIntState(onboardingSettings.agreementRevision)
        .combinePair(readSettings.observeBoolean(onboardingSettings.completedLicensePrompt))
        .map { (agreementRevision, completedLicense) ->
            OnboardingState(
                agreementRequired = agreementRevision != AGREEMENT_REVISION,
                licensePromptRequired = !completedLicense,
            )
        }

    fun setUserAgreed() {
        writeSettings.setInt(onboardingSettings.agreementRevision, AGREEMENT_REVISION)
    }

    fun setLicense(callsign: String, passcode: String) {
        writeSettings.setString(onboardingSettings.callsign, callsign.trim())
        writeSettings.setString(onboardingSettings.passcode, passcode.trim())
        writeSettings.setBoolean(onboardingSettings.completedLicensePrompt, true)
    }
}
