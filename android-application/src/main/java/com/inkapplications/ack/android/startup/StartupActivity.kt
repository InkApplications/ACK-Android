package com.inkapplications.ack.android.startup

import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.component
import com.inkapplications.ack.android.onboard.OnboardActivity
import com.inkapplications.ack.android.onboard.OnboardingStateAccess
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
