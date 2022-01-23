package com.inkapplications.ack.android.ui.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

data class ColorVariant(
    val brand: Color,
    val onBrand: Color,
    val foreground: Color,
    val background: Color,
    val surface: Color,
    val error: Color,
    val onError: Color,
    val light: Boolean,
) {
    val materialColors = Colors(
        primary = brand,
        primaryVariant = brand,
        secondary = brand,
        secondaryVariant = brand,
        background = background,
        surface = surface,
        error = error,
        onPrimary = onBrand,
        onSecondary = onBrand,
        onBackground = foreground,
        onSurface = foreground,
        onError = onError,
        isLight = light,
    )
}
