package com.inkapplications.ack.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun IconRow(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    Icon(icon, null, tint = AckTheme.colors.brand, modifier = Modifier.padding(AckTheme.dimensions.icon))
    Text(text)
}

@Composable
fun IconRow(
    icon: Painter,
    text: String,
    modifier: Modifier = Modifier,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    Icon(icon, null, tint = AckTheme.colors.brand, modifier = Modifier.padding(AckTheme.dimensions.icon))
    Text(text)
}
