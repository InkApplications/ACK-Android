package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.settings.*
import com.inkapplications.android.extensions.IntegerResources
import dagger.Reusable
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Provides information access relevant to onboarding in the application.
 */
@Reusable
class OnboardingStateAccess @Inject constructor(
    readSettings: SettingsReadAccess,
    private val writeSettings: SettingsWriteAccess,
    private val onboardingSettings: OnboardSettings,
    connectionSettings: ConnectionSettings,
    integers: IntegerResources,
) {
    private val latestRevision = integers.getInteger(R.integer.usage_revision)
    private val agreementRevision = readSettings.observeInt(onboardingSettings.agreementRevision)
    private val completedLicensePrompt = readSettings.observeBoolean(onboardingSettings.completedLicensePrompt)
    private val address = readSettings.observeData(connectionSettings.address)
    private val passcode = readSettings.observeData(connectionSettings.passcode)

    /**
     * Data needed to render the onboarding screens.
     */
    val onboardingData = agreementRevision
        .map { agreed ->
            OnboardingStateFactory.OnboardingData(
                latestRevision = latestRevision,
                agreementRevision = agreed,
            )
        }
        .combine(completedLicensePrompt) { data, completedLicense ->
            data.copy(completedLicense = completedLicense)
        }
        .combine(address) { data, address ->
            data.copy(currentAddress = address)
        }
        .combine(passcode) { data, passcode ->
            data.copy(currentPasscode = passcode)
        }

    /**
     * Whether onboarding is considered completed.
     */
    val finished = combine(agreementRevision, completedLicensePrompt) { agreement, licenseComplete ->
        agreement == latestRevision && licenseComplete
    }

    /**
     * Update the latest policy agreement to the current revision.
     */
    fun setUserAgreed() {
        writeSettings.setInt(onboardingSettings.agreementRevision, latestRevision)
    }

    /**
     * Set the license prompt flag as completed.
     */
    fun setLicensePromptCompleted() {
        writeSettings.setBoolean(onboardingSettings.completedLicensePrompt, true)
    }
}
