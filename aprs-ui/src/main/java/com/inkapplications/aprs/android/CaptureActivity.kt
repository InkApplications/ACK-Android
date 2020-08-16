package com.inkapplications.aprs.android

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.inkapplications.aprs.android.log.LogFragment
import com.inkapplications.aprs.android.map.MapFragment
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.kotlin.collectOn
import kimchi.Kimchi
import kotlinx.android.synthetic.main.capture.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

private const val RECORD_AUDIO_REQUEST = 45653

class CaptureActivity: AppCompatActivity() {
    private val mapFragment by lazy { MapFragment() }
    private val logFragment by lazy { LogFragment() }
    private lateinit var foreground: CoroutineScope
    private var recording: Job? = null
    private lateinit var aprs: AprsAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.capture)
        aprs = component.aprs()

        supportFragmentManager.beginTransaction()
            .replace(R.id.capture_stage, mapFragment)
            .commit()
        capture_navigation.setOnNavigationItemSelectedListener(::onNavigationClick)
        capture_mic.setOnClickListener(::onMicEnableClick)
        capture_mic_off.setOnClickListener(::onMicDisableClick)
    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()
    }

    private fun onMicEnableClick(view: View) {
        Kimchi.info("Enable Audio Recording Clicked")
        when(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PackageManager.PERMISSION_GRANTED -> onRecordAudio()
            else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_AUDIO_REQUEST -> if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) onRecordAudio()
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun onRecordAudio() {
        Kimchi.info("Start Recording")
        capture_mic.visibility = GONE
        capture_mic_off.visibility = VISIBLE
        recording = aprs.incoming.collectOn(foreground) {
            Kimchi.debug("APRS Packet Recorded: $it")
        }
    }

    private fun onMicDisableClick(view: View) {
        Kimchi.info("Stop Recording Clicked")
        recording?.cancel()
        recording = null
        capture_mic.visibility = VISIBLE
        capture_mic_off.visibility = GONE
    }

    private fun onNavigationClick(item: MenuItem): Boolean {
        Kimchi.info("Navigate to ${item.title}")
        when (item.itemId) {
            R.id.menu_capture_map -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.capture_stage, mapFragment)
                    .commit()
            }
            R.id.menu_capture_log -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.capture_stage, logFragment)
                    .commit()
            }
            else -> return false
        }
        return true
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}
