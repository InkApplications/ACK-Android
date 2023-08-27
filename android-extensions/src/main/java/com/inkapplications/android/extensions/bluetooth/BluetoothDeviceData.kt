package com.inkapplications.android.extensions.bluetooth

/**
 * Plain Data about a bluetooth device, de-coupled from Android's active model.
 */
data class BluetoothDeviceData(
    val address: String,
    val alias: String?,
    val name: String,
    val majorClass: Int?,
    val deviceClass: Int?,
    val bondState: Int,
)
