package com.inkapplications.ack.android.tnc

/**
 * Actions that can be taken from the bluetooth device list.
 */
interface DeviceListController {
    /**
     * Invoked when the user clicks the connect icon for a device.
     */
    fun onDeviceConnectClick(device: DeviceItem)

    /**
     * Invoked when the user clicks on the close icon for the screen.
     */
    fun onCloseClick()
}
