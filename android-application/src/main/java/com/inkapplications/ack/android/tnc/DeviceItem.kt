package com.inkapplications.ack.android.tnc

import com.inkapplications.android.extensions.bluetooth.BluetoothDeviceData

/**
 * Represents a bluetooth device and its connection state.
 */
sealed interface DeviceItem {
    /**
     * Technical data about the bluetooth device.
     */
    val data: BluetoothDeviceData

    /**
     * User-friendly name for the device.
     */
    val name: String

    /**
     * An unpaired bluetooth device.
     */
    data class Unpaired(
        override val name: String,
        override val data: BluetoothDeviceData,
    ): DeviceItem

    /**
     * A device that is paired, but not connected.
     */
    data class NotConnected(
        override val name: String,
        override val data: BluetoothDeviceData,
        /**
         * Whether automatic reconnection is enabled for this device.
         */
        val reconnect: Boolean,
    ): DeviceItem

    /**
     * A device that is currently being connected to.
     */
    data class Connecting(
        override val name: String,
        override val data: BluetoothDeviceData,
    ): DeviceItem

    /**
     * A connected bluetooth device.
     */
    data class Connected(
        override val name: String,
        override val data: BluetoothDeviceData,
        /**
         * Whether automatic reconnection is enabled for this device.
         */
        val reconnect: Boolean,
    ): DeviceItem
}
