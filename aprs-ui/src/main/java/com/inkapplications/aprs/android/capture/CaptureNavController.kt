package com.inkapplications.aprs.android.capture

import com.inkapplications.aprs.android.capture.log.LogItemState

interface CaptureNavController {
    fun onRecordingEnableClick()
    fun onRecordingDisableClick()
    fun onLocationEnableClick()
    fun onLocationDisableClick()
    fun onInternetServiceEnableClick()
    fun onInternetServiceDisableClick()
    fun onLogItemClick(log: LogItemState)
    fun onSettingsClick()
}
