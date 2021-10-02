package com.inkapplications.aprs.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    Text(label, style = AprsTheme.Typography.caption, color = AprsTheme.Colors.brand, modifier = Modifier.padding(end = AprsTheme.Spacing.icon))
    Text(value, style = AprsTheme.Typography.body)
}
