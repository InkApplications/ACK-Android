package com.inkapplications.ack.android.onboard

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.component
import com.inkapplications.ack.android.settings.SettingsAccess
import com.inkapplications.ack.android.settings.license.LicensePromptValidator
import kimchi.Kimchi
import kimchi.analytics.Property
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

class OnboardActivity: ExtendedActivity(), UserAgreementController {
    private lateinit var stateAccess: OnboardingStateAccess
    private lateinit var settingsAccess: SettingsAccess
    private lateinit var licensePromptValidator: LicensePromptValidator

    override fun onCreate() {
        super.onCreate()

        stateAccess = component.onboardingStateAccess()
        settingsAccess = component.settingsAccess()
        licensePromptValidator = component.licenseValidator()

        setContent {
            val licenseFieldState = settingsAccess.licensePromptFieldValues.collectAsState(null).value

            if (licenseFieldState != null) {
                OnboardScreen(
                    state = stateAccess.screenState.collectAsState(OnboardingState()),
                    userAgreementController = this,
                    initialLicensePromptFieldValues = licenseFieldState,
                    licenseValidator = licensePromptValidator,
                    onLicenseContinueClick = ::onLicenseContinueClick,
                )
            }
        }

        lifecycleScope.launchWhenCreated {
            stateAccess.screenState
                .filter { it.finished }
                .collect { onCompleted() }
        }
    }

    private fun onLicenseContinueClick(values: LicensePromptFieldValues) {
        Kimchi.trackEvent("onboard_license", listOf(
            Property.IntProperty("license_callsign_provided", if (values.callsign.isBlank()) 0 else 1),
            Property.IntProperty("license_passcode_provided", if (values.passcode.isBlank()) 0 else 1),
        ))
        settingsAccess.setLicense(values)
        stateAccess.setLicensePromptCompleted()
    }

    private fun onCompleted() {
        Kimchi.trackEvent("onboard_complete")
        startActivity(CaptureActivity::class)
        finish()
    }

    override fun onTermsAgreeClick() {
        Kimchi.trackEvent("onboard_agreement")
        stateAccess.setUserAgreed()
    }

    override fun onTermsDeclineClick() {
        finish()
    }
}
