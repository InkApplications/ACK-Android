package com.inkapplications.ack.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.android.extensions.control.ControlState

@Composable
fun StateLabelledIconButton(
    icon: ImageVector,
    title: String,
    onClick: ((ControlState) -> Unit) = {},
    state: ControlState,
    modifier: Modifier = Modifier,
) = when (state) {
    ControlState.On -> LabelledIconButton(
        title = title,
        icon = icon,
        backgroundColor = AckTheme.colors.accent,
        iconColor = AckTheme.colors.onAccent,
        onClick = { onClick(state) },
        modifier = modifier,
    )
    ControlState.Off -> LabelledIconButton(
        title = title,
        icon = icon,
        onClick = { onClick(state) },
        modifier = modifier,
        backgroundColor = AckTheme.colors.surface,
        border = BorderStroke(1.dp, AckTheme.colors.accent),
        iconColor = AckTheme.colors.foregroundInactive,
    )
    ControlState.Disabled -> LabelledIconButton(
        title = title,
        icon = icon,
        onClick = { onClick(state) },
        modifier = modifier.alpha(.6f),
        enabled = false,
        backgroundColor = AckTheme.colors.surface,
        border = BorderStroke(1.dp, AckTheme.colors.foregroundInactive),
        iconColor = AckTheme.colors.foregroundInactive,
    )
    ControlState.Hidden -> {}
}
