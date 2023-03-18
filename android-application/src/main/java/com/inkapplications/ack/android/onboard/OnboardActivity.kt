package com.inkapplications.ack.android.onboard

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.settings.SettingsAccess
import com.inkapplications.ack.android.settings.license.LicensePromptValidator
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.Property
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

@AndroidEntryPoint
class OnboardActivity: ExtendedActivity(), UserAgreementController {
    @Inject
    lateinit var stateAccess: OnboardingStateAccess

    @Inject
    lateinit var settingsAccess: SettingsAccess

    @Inject
    lateinit var licensePromptValidator: LicensePromptValidator

    override fun onCreate() {
        super.onCreate()

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
