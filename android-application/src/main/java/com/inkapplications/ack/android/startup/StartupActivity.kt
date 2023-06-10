package com.inkapplications.ack.android.startup

import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.onboard.OnboardActivity
import com.inkapplications.ack.android.onboard.OnboardingStateAccess
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * A screen for initializing application settings.
 */
@AndroidEntryPoint
class StartupActivity: ExtendedActivity() {
    /**
     * Delay time for the splash screen.
     *
     * This is so the splash screen doesn't flash for too short of a time and
     * appear as jank. Since the initializers are run asynchronously, this will
     * not increase the load time, but set a minimum load time.
     */
    private val delayTime = .5.seconds

    @Inject
    lateinit var initJob: InitJob

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
            delay(delayTime)
            initJob.join()

            if (onboardingStateAccess.finished.first()) {
                startActivity(CaptureActivity::class)
            } else {
                startActivity(OnboardActivity::class)
            }
            finish()
        }
    }
}
