package com.inkapplications.ack.android.tnc

import android.annotation.SuppressLint
import androidx.activity.compose.setContent
import com.inkapplications.ack.data.drivers.TncDriver
import com.inkapplications.android.extensions.ExtendedActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Activity that displays bluetooth TNC devices and manages connections.
 */
@AndroidEntryPoint
class ConnectTncActivity: ExtendedActivity(), DeviceListController {
    @Inject
    lateinit var bluetooth: TncDriver

    override fun onCreate() {
        super.onCreate()

        setContent {
            ConnectTncScreen(this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDeviceConnectClick(device: DeviceItem) {
        bluetooth.connectDevice(device.data)
    }

    override fun onDisconnect() {
        bluetooth.disconnect()
    }

    override fun onCloseClick() {
        finish()
    }
}
