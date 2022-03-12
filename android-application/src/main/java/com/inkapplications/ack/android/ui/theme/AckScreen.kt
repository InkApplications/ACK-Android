package com.inkapplications.ack.android.ui.theme

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AckScreen(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = AckTheme.colors.materialColors,
        typography = AckTheme.typography.material,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides AckTheme.colors.foreground,
        ) { content() }
    }
}
