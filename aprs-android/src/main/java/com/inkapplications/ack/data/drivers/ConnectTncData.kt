package com.inkapplications.ack.data.drivers

import com.inkapplications.android.extensions.bluetooth.BluetoothDeviceData

/**
 * TNC Connection information.
 *
 * This represents the current state of the TNC connection driver.
 */
data class ConnectTncData(
    /**
     * List of bluetooth devices discovered that may be TNC devices.
     */
    var discoveredDevices: List<BluetoothDeviceData>,

    /**
     * Device that is currently being connected to.
     */
    var connectingDevice: BluetoothDeviceData?,

    /**
     * Device that is currently connected to.
     */
    var connectedDevice: BluetoothDeviceData?,
)
