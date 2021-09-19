package com.inkapplications.aprs.android.onboard

import com.inkapplications.aprs.android.connection.ConnectionSettings
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.SettingsWriteAccess
import com.inkapplications.aprs.android.settings.observeBoolean
import dagger.Reusable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Reusable
class OnboardingStateAccess @Inject constructor(
    readSettings: SettingsReadAccess,
    private val writeSettings: SettingsWriteAccess,
    private val onboardingSettings: OnboardSettings,
    private val connectionSettings: ConnectionSettings,
) {
    private val callsignError = MutableStateFlow<String?>(null)
    private val passcodeError = MutableStateFlow<String?>(null)

    val screenState = readSettings.observeIntState(onboardingSettings.agreementRevision)
        .map {
            OnboardingState(agreementRequired = it != AGREEMENT_REVISION)
        }
        .combine(readSettings.observeBoolean(onboardingSettings.completedLicensePrompt))  { state, completedLicense ->
            state.copy(licensePromptRequired = !completedLicense)
        }
        .combine(callsignError) { state, callsignError ->
            state.copy(callsignError = callsignError)
        }
        .combine(passcodeError) { state, passcodeError ->
            state.copy(passcodeError = passcodeError)
        }

    fun setUserAgreed() {
        writeSettings.setInt(onboardingSettings.agreementRevision, AGREEMENT_REVISION)
    }

    fun setLicense(callsign: String, passcode: String) {
        val cleanCallsign = callsign.trim()
        val cleanPasscode = passcode.trim().toIntOrNull()

        val validCallsign = LicenseValidator.validateCallsign(callsign) || callsign.isBlank()
        val validPasscode = cleanCallsign.isBlank() || passcode.isBlank() || (cleanPasscode != null && LicenseValidator.validatePasscode(cleanCallsign, cleanPasscode))

        callsignError.value = if (!validCallsign) "Invalid callsign" else null
        passcodeError.value = if (!validPasscode) "Incorrect Passcode" else null

        if (!validCallsign || !validPasscode) return

        writeSettings.setString(connectionSettings.callsign, cleanCallsign)
        writeSettings.setInt(connectionSettings.passcode, cleanPasscode ?: -1)
        writeSettings.setBoolean(onboardingSettings.completedLicensePrompt, true)
    }
}
