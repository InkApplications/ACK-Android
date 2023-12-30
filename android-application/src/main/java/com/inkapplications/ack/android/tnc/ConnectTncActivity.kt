package com.inkapplications.ack.android.tnc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.stringProperty
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val ARG_BACKGROUND_INTENT = "background_intent"

/**
 * Activity that displays bluetooth TNC devices and manages connections.
 */
@AndroidEntryPoint
class ConnectTncActivity: ExtendedActivity(), DeviceListController {
    @Inject
    lateinit var drivers: PacketDrivers

    private lateinit var backgroundService: Intent

    override fun onCreate() {
        super.onCreate()

        Kimchi.trackScreen("connect_tnc")

        backgroundService = intent.getParcelableExtra(ARG_BACKGROUND_INTENT)!!

        setContent {
            ConnectTncScreen(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDeviceConnectClick(device: DeviceItem) {
        Kimchi.trackEvent("connect_tnc_connect", listOf(
            stringProperty("device_name", device.name)
        ))
        lifecycleScope.launch {
            drivers.tncDriver.selectDevice(device.data)
            startService(backgroundService)
            drivers.tncDriver.connectionState.filter { it == DriverConnectionState.Connected }.first()
            finish()
        }
    }

    override fun onCloseClick() {
        Kimchi.trackEvent("connect_tnc_close")
        finish()
    }
}

fun Activity.startConnectTncActivity(
    backgroundService: Intent,
) {
    Kimchi.trackNavigation("connect_tnc")
    startActivity(ConnectTncActivity::class) {
        putExtra(ARG_BACKGROUND_INTENT, backgroundService)
    }
}
