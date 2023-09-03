package com.inkapplications.ack.data.drivers

import android.Manifest
import android.bluetooth.BluetoothClass
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TncDriver @Inject constructor(
    private val bluetoothAccess: BluetoothDeviceAccess,
    private val packetStorage: PacketStorage,
    private val ack: AprsCodec,
): PacketDriver {
    /**
     * Bluetooth Serial Port Profile (SPP) UUID.
     */
    private val SPP_UID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val mutableConnectingDevice = MutableStateFlow<BluetoothDeviceData?>(null)
    private val mutableConnectedDevice = MutableStateFlow<BluetoothDeviceData?>(null)
    private val inputStream = MutableStateFlow<InputStream?>(null)
    private val outputStream = MutableStateFlow<OutputStream?>(null)
    private val networkDevices = bluetoothAccess.devices
        .filterItems { it.majorClass == BluetoothClass.Device.Major.NETWORKING }

    val connectedDevice: StateFlow<BluetoothDeviceData?> = mutableConnectedDevice

    val deviceData: Flow<ConnectTncData> = combine(
        connectedDevice,
        mutableConnectingDevice,
        networkDevices,
    ) { connected, connecting, devices ->
        ConnectTncData(
            connectedDevice = connected,
            connectingDevice = connecting,
            discoveredDevices = devices,
        )
    }

    private val kissData = inputStream
        .filterNotNull()
        .flatMapLatest { it.kissData() }

    override val incoming = MutableSharedFlow<CapturedPacket>()

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    fun connectDevice(device: BluetoothDeviceData) {
        mutableConnectingDevice.value = device
        bluetoothAccess.connect(device, SPP_UID) { input, output ->
            mutableConnectedDevice.value = device
            mutableConnectingDevice.value = null
            inputStream.value = input
            outputStream.value = output
            mutableConnectedDevice.filter { it == null }.first()
            disconnect()
        }
    }

    fun disconnect() {
        mutableConnectingDevice.value = null
        inputStream.value = null
        outputStream.value = null
        mutableConnectedDevice.value = null
    }

    override suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig) {
        val data = ack.toAx25(packet, encodingConfig)
        outputStream.value?.writeKissData(data)
    }

    override suspend fun connect() {
        kissData.collect { data ->
            val packet = ack.fromAx25(data)
            val saved = packetStorage.save(data, packet, PacketSource.Tnc)
            incoming.emit(saved)
        }
    }
}
