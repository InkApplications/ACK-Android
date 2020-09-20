package com.inkapplications.aprs.android.capture

import android.Manifest
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.stopPropagation
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.capture.log.LogFragment
import com.inkapplications.aprs.android.capture.map.MapFragment
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.settings.SettingsActivity
import com.inkapplications.aprs.android.trackNavigation
import kimchi.Kimchi
import kotlinx.android.synthetic.main.capture.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val RECORD_AUDIO_REQUEST = 45653

class CaptureActivity: ExtendedActivity() {
    private val mapFragment by lazy { MapFragment() }
    private val logFragment by lazy { LogFragment() }
    private var recording: Job? = null
    private lateinit var captureEvents: CaptureEvents
    private lateinit var menuRecordDisabled: MenuItem
    private lateinit var menuRecordEnabled: MenuItem

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.capture)
        setSupportActionBar(capture_toolbar)
        captureEvents = component.captureEvents()

        supportFragmentManager.beginTransaction()
            .replace(R.id.capture_stage, mapFragment)
            .commit()
        capture_navigation.setOnNavigationItemSelectedListener(::onNavigationClick)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean = stopPropagation {
        menuInflater.inflate(R.menu.capture_toolbar, menu)
        menuRecordEnabled = menu.findItem(R.id.menu_capture_toolbar_mic_enabled)
        menuRecordDisabled = menu.findItem(R.id.menu_capture_toolbar_mic_disabled)
    }

    private fun enableRecording() {
        Kimchi.trackEvent("record_enable")
        when(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PackageManager.PERMISSION_GRANTED -> onRecordAudio()
            else -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_REQUEST)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_capture_toolbar_settings -> stopPropagation {
            Kimchi.trackNavigation("settings")
            startActivity(SettingsActivity::class)
        }
        R.id.menu_capture_toolbar_mic_disabled -> stopPropagation {
            enableRecording()
        }
        R.id.menu_capture_toolbar_mic_enabled -> stopPropagation {
            disableRecording()
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when {
            requestCode == RECORD_AUDIO_REQUEST -> if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                Kimchi.trackEvent("record_permission_grant")
                onRecordAudio()
            }
            requestCode == RECORD_AUDIO_REQUEST -> {
                Kimchi.trackEvent("record_permission_deny")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun onRecordAudio() {
        Kimchi.info("Start Recording")
        menuRecordEnabled.isVisible = true
        menuRecordDisabled.isVisible = false
        recording = foregroundScope.launch { captureEvents.listenForPackets() }
    }

    private fun disableRecording() {
        Kimchi.trackEvent("record_disable")
        menuRecordEnabled.isVisible = false
        menuRecordDisabled.isVisible = true
        recording?.cancel()
        recording = null
    }

    private fun onNavigationClick(item: MenuItem): Boolean {
        Kimchi.info("Navigate to ${item.title}")
        when (item.itemId) {
            R.id.menu_capture_map -> {
                Kimchi.trackNavigation("map")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.capture_stage, mapFragment)
                    .commit()
            }
            R.id.menu_capture_log -> {
                Kimchi.trackNavigation("log")
                supportFragmentManager.beginTransaction()
                    .replace(R.id.capture_stage, logFragment)
                    .commit()
            }
            else -> return false
        }
        return true
    }
}
