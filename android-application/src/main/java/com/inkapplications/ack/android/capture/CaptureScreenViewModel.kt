package com.inkapplications.ack.android.capture

import com.inkapplications.android.extensions.control.ControlState

data class CaptureScreenViewModel(
    val recordingState: ControlState = ControlState.Hidden,
    val internetServiceState: ControlState = ControlState.Hidden,
    val transmitState: ControlState = ControlState.Hidden,
)
