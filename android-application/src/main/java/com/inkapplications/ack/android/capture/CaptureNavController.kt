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
     * Invoked when the user clicks on the button to enable the driver connection.
     */
    fun onConnectClick()

    /**
     * Invoked when the user clicks on the button to disable the driver connection.
     */
    fun onDisconnectClick()

    /**
     * Invoked when the user clicks on the location transmit enable button.
     */
    fun onEnableLocationTransmitClick()

    /**
     * Invoked when the user clicks on the location transmit disable button.
     */
    fun onDisableLocationTransmitClick()

    /**
     * Invoked when the user selects a new radio driver.
     */
    fun onDriverSelected(selection: DriverSelection)
}
