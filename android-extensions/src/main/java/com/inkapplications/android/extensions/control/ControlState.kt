package com.inkapplications.android.extensions.control

import androidx.compose.runtime.Composable

enum class ControlState {
    On,
    Off,
    Disabled,
    Hidden,
}

@Composable
inline fun ControlState.whenOn(action: @Composable () -> Unit) {
    if (this == ControlState.On) action()
}

@Composable
inline fun ControlState.whenOff(action: @Composable () -> Unit) {
    if (this == ControlState.Off) action()
}

@Composable
inline fun ControlState.whenDisabled(action: @Composable () -> Unit) {
    if (this == ControlState.Disabled) action()
}

@Composable
inline fun ControlState.whenHidden(action: @Composable () -> Unit) {
    if (this == ControlState.Hidden) action()
}

@Composable
inline fun ControlState.whenVisible(action: @Composable () -> Unit) {
    if (this != ControlState.Hidden) action()
}
