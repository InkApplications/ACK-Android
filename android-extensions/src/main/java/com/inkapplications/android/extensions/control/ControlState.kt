package com.inkapplications.android.extensions.control

import androidx.compose.runtime.Composable

enum class ControlState {
    Enabled,
    Disabled,
    Hidden,
}

@Composable
inline fun ControlState.whenEnabled(action: @Composable () -> Unit) {
    if (this == ControlState.Enabled) action()
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
