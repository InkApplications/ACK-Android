package com.inkapplications.aprs.android.startup

import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.capture.CaptureActivity
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.onboard.OnboardActivity
import com.inkapplications.aprs.android.onboard.OnboardingStateAccess
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * A screen for initializing application settings.
 */
class StartupActivity: ExtendedActivity() {
    private lateinit var initializer: ApplicationInitializer
    private lateinit var onboardingStateAccess: OnboardingStateAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializer = component.initializer()
        onboardingStateAccess = component.onboardingStateAccess()

        setContent {
            StartupScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        foregroundScope.launch {
            initializer.initialize(application)
            if (onboardingStateAccess.screenState.first().finished) {
                startActivity(CaptureActivity::class)
            } else {
                startActivity(OnboardActivity::class)
            }
            finish()
        }
    }
}
