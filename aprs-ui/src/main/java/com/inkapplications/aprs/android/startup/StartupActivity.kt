package com.inkapplications.aprs.android.startup

import android.os.Bundle
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.fadeIn
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.BuildConfig
import com.inkapplications.aprs.android.capture.CaptureActivity
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import kotlinx.android.synthetic.main.splash.*
import kotlinx.coroutines.*

/**
 * A screen for initializing application settings.
 */
class StartupActivity: ExtendedActivity() {
    private lateinit var initializer: ApplicationInitializer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializer = component.initializer()
        setContentView(R.layout.splash)
        splash_caption.text = getString(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString())
        splash_wave.fadeIn()
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