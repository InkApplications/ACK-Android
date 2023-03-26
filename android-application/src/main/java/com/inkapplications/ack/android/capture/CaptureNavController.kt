package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.log.LogItemViewState

/**
 * Capture navigation and control panel options.
 */
interface CaptureNavController {
    /**
     * Invoked when the user clicks the enable audio capture button.
     */
    fun onAudioCaptureEnableClick()

    /**
     * Invoked when the user clicks the disable audio capture button.
     */
    fun onAudioCaptureDisableClick()

    /**
     * Invoked when the user clicks the enable audio transmit button.
     */
    fun onAudioTransmitEnableClick()

    /**
     * Invoked when the user clicks the disable audio transmit button.
     */
    fun onAudioTransmitDisableClick()

    /**
     * Invoked when the user clicks the enable internet capture button.
     */
    fun onInternetCaptureEnableClick()

    /**
     * Invoked when the user clicks the enable internet capture button.
     */
    fun onInternetCaptureDisableClick()

    /**
     * Invoked when the user clicks the disable internet transmit button.
     */
    fun onInternetTransmitEnableClick()

    /**
     * Invoked when the user clicks the disable internet transmit button.
     */
    fun onInternetTransmitDisableClick()

    /**
     * Invoked when the user clicks to enable location tracking on the map.
     */
    fun onLocationEnableClick()

    /**
     * Invoked when the user clicks to disable location tracking on the map.
     */
    fun onLocationDisableClick()

    /**
     * Invoked when the user clicks on the highlighted log item.
     *
     * Note, this is the popover overlay, not the pin click.
     */
    fun onLogMapItemClick(log: LogItemViewState)

    /**
     * Invoked when the user clicks on the settings button.
     */
    fun onSettingsClick()
}
