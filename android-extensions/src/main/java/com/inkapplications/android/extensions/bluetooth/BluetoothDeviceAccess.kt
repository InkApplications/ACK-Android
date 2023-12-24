package com.inkapplications.android.extensions.bluetooth

import android.Manifest
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

interface BluetoothDeviceAccess {
    @get:RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    val devices: Flow<List<BluetoothDeviceData>>

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    suspend fun connect(
        device: BluetoothDeviceData,
        uuid: UUID,
        onConnect: suspend (InputStream, OutputStream) -> Unit,
    )
}

