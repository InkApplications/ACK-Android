package com.inkapplications.ack.data.drivers

import android.Manifest
import android.bluetooth.BluetoothClass
import android.os.Build
import androidx.annotation.RequiresPermission
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.data.kiss.kissData
import com.inkapplications.ack.data.kiss.writeKissData
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.android.extensions.bluetooth.BluetoothDeviceAccess
import com.inkapplications.android.extensions.bluetooth.BluetoothDeviceData
import com.inkapplications.coroutines.filterItems
import com.inkapplications.standard.throwCancels
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class TncDriver internal constructor(
    private val bluetoothAccess: BluetoothDeviceAccess,
    private val packetStorage: PacketStorage,
    private val ack: AprsCodec,
    private val runScope: CoroutineScope,
    private val logger: KimchiLogger,
): PacketDriver {
    /**
     * Bluetooth Serial Port Profile (SPP) UUID.
     */
    private val SPP_UID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val runJob = MutableStateFlow<Job?>(null)
    private val selectedDevice = MutableStateFlow<BluetoothDeviceData?>(null)
    private val deviceConnectionState = MutableStateFlow(DriverConnectionState.Disconnected)
    override val connectionState: Flow<DriverConnectionState> = combine(deviceConnectionState, selectedDevice) { connection, device ->
        when {
            device == null -> DriverConnectionState.Disconnected
            connection == DriverConnectionState.Disconnected -> DriverConnectionState.Connecting
            else -> connection
        }
    }
    override val receivePermissions: Set<String> = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.S -> setOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        else -> setOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    override val transmitPermissions: Set<String> = when {
        Build.VERSION.SDK_INT > Build.VERSION_CODES.S -> setOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        else -> setOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    private val outputStream = MutableStateFlow<OutputStream?>(null)
    private val networkDevices = bluetoothAccess.devices
        .filterItems { it.majorClass == BluetoothClass.Device.Major.NETWORKING }

    val deviceData: Flow<ConnectTncData> = combine(
        deviceConnectionState,
        selectedDevice,
        networkDevices,
    ) { connectionState, selectedDevice, devices ->
        ConnectTncData(
            connectedDevice = selectedDevice.takeIf { connectionState == DriverConnectionState.Connected },
            connectingDevice = selectedDevice.takeIf { connectionState == DriverConnectionState.Connecting },
            discoveredDevices = devices,
        )
    }

    override val incoming = MutableSharedFlow<CapturedPacket>()

    fun selectDevice(device: BluetoothDeviceData) {
        selectedDevice.value = device
    }

    override suspend fun disconnect() {
        deviceConnectionState.value = DriverConnectionState.Disconnecting
        runJob.value?.cancelAndJoin()
    }

    override suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig) {
        val data = ack.toAx25(packet, encodingConfig)
        outputStream.value?.writeKissData(data)
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    override suspend fun connect() {
        runJob.updateAndGet { priorJob ->
            priorJob?.takeIf { it.isActive } ?: launchNewJob()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    private fun launchNewJob(): Job {
        logger.debug("Launching new job for Bluetooth Packet Capture")
        return runScope.launch {
            try {
                selectedDevice
                    .filterNotNull()
                    .collectLatest { deviceData ->
                        deviceConnectionState.value = DriverConnectionState.Connecting
                        try {
                            bluetoothAccess.connect(
                                device = deviceData,
                                uuid = SPP_UID
                            ) { input, output ->
                                outputStream.value = output
                                deviceConnectionState.value = DriverConnectionState.Connected
                                input.kissData().collect { data ->
                                    runCatching {
                                        val packet = ack.fromAx25(data)
                                        val saved = packetStorage.save(data, packet, PacketSource.Tnc)
                                        incoming.emit(saved)
                                    }.throwCancels().onFailure {
                                        logger.error("Failed to capture packet", it)
                                    }
                                }
                            }
                        } catch (error: IOException) {
                            logger.warn("IO Error while connecting to TNC. Possibly during Disconnect.", error)
                        } finally {
                            outputStream.value = null
                            deviceConnectionState.value = DriverConnectionState.Disconnected
                        }
                    }
            } finally {
                selectedDevice.value = null
            }
        }
    }
}
