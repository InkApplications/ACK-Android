package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.android.capture.log.LogItemViewModel

interface CaptureNavController {
    fun onRecordingEnableClick()
    fun onRecordingDisableClick()
    fun onLocationEnableClick()
    fun onLocationDisableClick()
    fun onInternetServiceEnableClick()
    fun onInternetServiceDisableClick()
    fun onTransmitEnableClick()
    fun onTransmitDisableClick()
    fun onLogItemClick(log: LogItemViewModel)
    fun onSettingsClick()
}
