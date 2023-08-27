package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.log.LogItemViewState

/**
 * Capture navigation and control panel options.
 */
interface CaptureNavController {
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

    /**
     * Invoked when the user clicks on the device connection settings button.
     */
    fun onDeviceSettingsClick()

    /**
     * Invoked when the user clicks on the connected/disconnected button toggle.
     */
    fun onConnectionToggleClick()

    /**
     * Invoked when the user clicks on the transmit position toggle.
     */
    fun onPositionTransmitToggleClick()

    /**
     * Invoked when the user selects a new radio driver.
     */
    fun onDriverSelected(selection: DriverSelection)
}
