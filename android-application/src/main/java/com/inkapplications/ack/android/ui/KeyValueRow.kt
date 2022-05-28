package com.inkapplications.ack.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    Text(label, style = AckTheme.typography.caption, color = AckTheme.colors.primary, modifier = Modifier.padding(end = AckTheme.dimensions.icon))
    Text(value, style = AckTheme.typography.body)
}
