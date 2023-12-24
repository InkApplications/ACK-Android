package com.inkapplications.ack.android.tnc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.ack.data.drivers.PacketDrivers
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
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

        backgroundService = intent.getParcelableExtra(ARG_BACKGROUND_INTENT)!!

        setContent {
            ConnectTncScreen(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDeviceConnectClick(device: DeviceItem) {
        lifecycleScope.launch {
            drivers.tncDriver.selectDevice(device.data)
            startService(backgroundService)
            drivers.tncDriver.connectionState.filter { it == DriverConnectionState.Connected }.first()
            finish()
        }
    }

    override fun onCloseClick() {
        finish()
    }
}

fun Activity.startConnectTncActivity(
    backgroundService: Intent,
) {
    startActivity(ConnectTncActivity::class) {
        putExtra(ARG_BACKGROUND_INTENT, backgroundService)
    }
}
