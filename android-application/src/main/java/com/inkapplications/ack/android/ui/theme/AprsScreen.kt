package com.inkapplications.ack.android.ui.theme

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AprsScreen(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = AprsTheme.colors.materialColors,
        typography = AprsTheme.typography.material,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides AprsTheme.colors.foreground,
        ) { content() }
    }
}
