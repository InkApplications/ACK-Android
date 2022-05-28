package com.inkapplications.ack.android.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class ColorVariant(
    val primary: Color,
    val onPrimary: Color,
    val foreground: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onError: Color,
    val light: Boolean,
) {
    val foregroundInactive = foreground.copy(alpha = .5f)

    val materialColors = Colors(
        primary = primary,
        primaryVariant = primary,
        secondary = primary,
        secondaryVariant = primary,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onPrimary,
        onSecondary = onPrimary,
        onBackground = foreground,
        onSurface = foreground,
        onError = onError,
        isLight = light,
    )
}
