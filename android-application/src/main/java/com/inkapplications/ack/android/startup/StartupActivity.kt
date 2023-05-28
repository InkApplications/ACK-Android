package com.inkapplications.ack.android.startup

import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.onboard.OnboardActivity
import com.inkapplications.ack.android.onboard.OnboardingStateAccess
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A screen for initializing application settings.
 */
@AndroidEntryPoint
class StartupActivity: ExtendedActivity() {
    @Inject
    lateinit var initializer: ApplicationInitializer

    @Inject
    lateinit var onboardingStateAccess: OnboardingStateAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StartupScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        foregroundScope.launch {
            initializer.initialize(application)
            if (onboardingStateAccess.finished.first()) {
                startActivity(CaptureActivity::class)
            } else {
                startActivity(OnboardActivity::class)
            }
            finish()
        }
    }
}
