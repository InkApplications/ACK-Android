package com.inkapplications.aprs.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.log.LogActivity
import kotlinx.android.synthetic.main.dashboard.*

class DashboardActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        dashboard_log_button.setOnClickListener {
            startActivity(LogActivity::class)
        }
        val map = supportFragmentManager.findFragmentById(R.id.dashboard_map) as SupportMapFragment
        map.getMapAsync { map ->
            CameraUpdateFactory.newLatLngZoom(LatLng(45.0499576,-93.2584823), 10f).run {
                map.moveCamera(this)
            }
        }
    }
}
