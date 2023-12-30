package com.inkapplications.ack.android.onboard

import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.settings.SettingsAccess
import com.inkapplications.ack.android.settings.license.LicensePromptFieldValues
import com.inkapplications.ack.android.settings.license.LicensePromptValidator
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.Property
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Screen that shows required agreements and setup at first launch.
 */
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
        Kimchi.trackScreen("onboard")

        setContent {
            val viewModel: OnboardingViewModel = hiltViewModel()

            OnboardScreen(
                viewModel = viewModel,
                userAgreementController = this,
                licenseValidator = licensePromptValidator,
                onLicenseContinueClick = ::onLicenseContinueClick,
            )

            LaunchedEffect(null) {
                viewModel.screenState.first { it is OnboardingState.Complete }
                onCompleted()
            }
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
        Kimchi.trackEvent("onboard_terms_agreement")
        stateAccess.setUserAgreed()
    }

    override fun onTermsDeclineClick() {
        // Intentionally no logging.
        finish()
    }
}
