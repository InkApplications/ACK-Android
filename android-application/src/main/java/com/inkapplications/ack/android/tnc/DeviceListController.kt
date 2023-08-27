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
     * Invoked when the user clicks to disconnect a device.
     *
     * Note: this does not require a device to be passed in, as the
     * driver only currently allows a single device connection. It can
     * be assumed that this should disconnect any device, despite being
     * invoked from a specific device row in the UI.
     */
    fun onDisconnect()

    /**
     * Invoked when the user clicks on the close icon for the screen.
     */
    fun onCloseClick()
}
