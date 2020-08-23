package com.inkapplications.aprs.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inkapplications.android.extensions.fadeIn
import com.inkapplications.android.extensions.startActivity
import kotlinx.android.synthetic.main.splash.*
import kotlinx.coroutines.*

/**
 * A screen for initializing application settings.
 */
class StartupActivity: AppCompatActivity() {
    private lateinit var foreground: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        splash_caption.text = getString(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString())
        splash_wave.fadeIn()
    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()

        foreground.launch {
            delay(500)
            startActivity(CaptureActivity::class)
        }
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}