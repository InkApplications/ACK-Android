package com.inkapplications.aprs.android.onboard

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.lifecycleScope
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.capture.CaptureActivity
import com.inkapplications.aprs.android.component
import kimchi.Kimchi
import kimchi.analytics.Property
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

class OnboardActivity: ExtendedActivity() {
    private lateinit var stateAccess: OnboardingStateAccess

    override fun onCreate() {
        super.onCreate()

        stateAccess = component.onboardingStateAccess()

        setContent {
            OnboardScreen(
                state = stateAccess.screenState.collectAsState(OnboardingState()),
                onDeclineClick = ::finish,
                onAgreeClick = ::onAgreeClick,
                onLicenseContinueClick = ::onLicenseContinueClick,
            )
        }

        lifecycleScope.launchWhenCreated {
            stateAccess.screenState
                .filter { it.finished }
                .collect { onCompleted() }
        }
    }

    private fun onAgreeClick() {
        Kimchi.trackEvent("onboard_agreement")
        stateAccess.setUserAgreed()
    }

    private fun onLicenseContinueClick(callsign: String, passcode: String) {
        Kimchi.trackEvent("onboard_license", listOf(
            Property.IntProperty("license_callsign_provided", if (callsign.isBlank()) 0 else 1),
            Property.IntProperty("license_passcode_provided", if (passcode.isBlank()) 0 else 1),
        ))
        stateAccess.setLicense(callsign, passcode)
    }

    private fun onCompleted() {
        Kimchi.trackEvent("onboard_complete")
        startActivity(CaptureActivity::class)
        finish()
    }
}
