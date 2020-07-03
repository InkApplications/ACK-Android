package com.inkapplications.aprs.android

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.inkapplications.aprs.android.log.LogFragment
import com.inkapplications.aprs.android.map.MapFragment
import kotlinx.android.synthetic.main.capture.*

class CaptureActivity: AppCompatActivity() {
    private val mapFragment by lazy { MapFragment() }
    private val logFragment by lazy { LogFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.capture)

        capture_navigation.setOnNavigationItemSelectedListener(::onNavigationClick)
    }

    private fun onNavigationClick(item: MenuItem): Boolean {
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
}
