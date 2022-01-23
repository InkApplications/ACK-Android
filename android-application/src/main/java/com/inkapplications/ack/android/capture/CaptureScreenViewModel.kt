package com.inkapplications.ack.android.capture

data class CaptureScreenViewModel(
    val recordingEnabled: Boolean = false,
    val internetServiceEnabled: Boolean = false,
    val internetServiceVisible: Boolean = false,
    val transmitState: Boolean = false,
)
