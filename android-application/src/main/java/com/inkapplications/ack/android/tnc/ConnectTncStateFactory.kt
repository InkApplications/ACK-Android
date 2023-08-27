package com.inkapplications.ack.android.tnc

import android.bluetooth.BluetoothDevice
import com.inkapplications.ack.data.drivers.ConnectTncData
import dagger.Reusable
import javax.inject.Inject

/**
 * Create view model data from the current connection state.
 */
@Reusable
class ConnectTncStateFactory @Inject constructor() {
    fun create(data: ConnectTncData): ConnectTncState {
        val connectedDevice = data.connectedDevice
        val connectingDevice = data.connectingDevice

        val devices = data.discoveredDevices.map {
            when {
                it == connectedDevice -> DeviceItem.Connected(
                    name = it.name,
                    data = it,
                    reconnect = false,
                )
                it == connectingDevice -> DeviceItem.Connecting(
                    name = it.name,
                    data = it,
                )
                it.bondState == BluetoothDevice.BOND_BONDED -> DeviceItem.NotConnected(
                    name = it.name,
                    data = it,
                    reconnect = false,
                )
                else -> DeviceItem.Unpaired(
                    name = it.name,
                    data = it,
                )
            }
        }

        return when {
            data.discoveredDevices.isEmpty() -> ConnectTncState.Discovering.Empty
            else -> ConnectTncState.Discovering.DeviceList(devices)
        }
    }
}
