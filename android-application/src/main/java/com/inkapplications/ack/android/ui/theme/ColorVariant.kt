package com.inkapplications.ack.android.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class ColorVariant(
    val accent: Color,
    val onAccent: Color,
    val foreground: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onError: Color,
    val light: Boolean,
) {
    val foregroundInactive = foreground.copy(alpha = .5f)
    val surfaceInactive = surface.copy(alpha = .5f)

    val materialColors = Colors(
        primary = accent,
        primaryVariant = accent,
        secondary = accent,
        secondaryVariant = accent,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onAccent,
        onSecondary = onAccent,
        onBackground = foreground,
        onSurface = foreground,
        onError = onError,
        isLight = light,
    )
}
