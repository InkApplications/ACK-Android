package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.capture.log.LogItemViewModel

interface CaptureNavController {
    fun onAudioCaptureEnableClick()
    fun onAudioCaptureDisableClick()

    fun onAudioTransmitEnableClick()
    fun onAudioTransmitDisableClick()

    fun onLocationEnableClick()
    fun onLocationDisableClick()

    fun onInternetCaptureEnableClick()
    fun onInternetCaptureDisableClick()

    fun onInternetTransmitEnableClick()
    fun onInternetTransmitDisableClick()

    fun onLogMapItemClick(log: LogItemViewModel)
    fun onSettingsClick()
}
