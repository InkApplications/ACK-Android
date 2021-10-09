package com.inkapplications.aprs.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inkapplications.aprs.android.ui.theme.AprsTheme

@Composable
fun KeyValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
    Text(label, style = AprsTheme.typography.caption, color = AprsTheme.colors.brand, modifier = Modifier.padding(end = AprsTheme.spacing.icon))
    Text(value, style = AprsTheme.typography.body)
}
