package com.inkapplications.android.extensions.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

/**
 * Provides access to bluetooth devices via event streams.
 */
internal class AndroidBluetoothAccess(
    context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
): BluetoothDeviceAccess {
    /**
     * Flow that starts bluetooth discovery and emits discovered devices as they're found.
     */
    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    private val deviceEmitter: Flow<BluetoothDeviceData> = callbackFlow {
        val deviceFoundReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device: BluetoothDevice = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                            ?: return
                    } else {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as? BluetoothDevice
                            ?: return
                    }
                    trySend(
                        BluetoothDeviceData(
                            address = device.address,
                            alias = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) device.alias else null,
                            name = device.name.orEmpty(),
                            majorClass = device.bluetoothClass?.majorDeviceClass,
                            deviceClass = device.bluetoothClass?.deviceClass,
                            bondState = device.bondState,
                        )
                    )
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(deviceFoundReceiver, filter)
        bluetoothAdapter.startDiscovery()
        awaitClose { context.unregisterReceiver(deviceFoundReceiver) }
    }

    /**
     * An accumulating list of discovered devices.
     *
     * Starting this flow will start bluetooth discovery and collect discovered
     * devices into a list, de-duplicated by device address.
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override val devices: Flow<List<BluetoothDeviceData>> = deviceEmitter.scan(emptyList()) { acc, device ->
        if (acc.any { it.address == device.address }) acc else acc + device
    }

    /**
     * Connect to a bluetooth device.
     *
     * This starts a connection to the specified bluetooth device and invokes
     * the [onConnect] callback with the input and output streams for the connection.
     * The [onConnect] method should remain suspended to keep the connection open.
     * Once the method returns, the bluetooth socket will be closed.
     */
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override suspend fun connect(
        device: BluetoothDeviceData,
        uuid: UUID,
        onConnect: suspend (InputStream, OutputStream) -> Unit,
    ) {
        val bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address)
        bluetoothAdapter.cancelDiscovery()
        val socket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid)
        socket.connect()
        onConnect(socket.inputStream, socket.outputStream)
        socket.close()
    }
}
