package com.inkapplications.ack.android.capture

import com.inkapplications.android.extensions.control.ControlState

data class CaptureScreenViewModel(
    val audioCaptureState: ControlState = ControlState.Hidden,
    val internetCaptureState: ControlState = ControlState.Hidden,
    val internetTransmitState: ControlState = ControlState.Hidden,
    val audioTransmitState: ControlState = ControlState.Hidden,
)
