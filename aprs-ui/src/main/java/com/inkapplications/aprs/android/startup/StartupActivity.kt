package com.inkapplications.aprs.android.startup

import android.os.Bundle
import androidx.activity.compose.setContent
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.capture.CaptureActivity
import com.inkapplications.aprs.android.component
import kotlinx.coroutines.*

/**
 * A screen for initializing application settings.
 */
class StartupActivity: ExtendedActivity() {
    private lateinit var initializer: ApplicationInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializer = component.initializer()

        setContent {
            StartupScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        foregroundScope.launch {
            initializer.initialize(application)
            startActivity(CaptureActivity::class)
            finish()
        }
    }
}
