package com.inkapplications.ack.android.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object AckTheme {
    private val primaryColor
        @Composable
        @ReadOnlyComposable
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            colorResource(android.R.color.system_accent1_400)
        } else {
            ColorPalette.brand
        }

    val darkColors
        @Composable
        @ReadOnlyComposable
        get() = ColorVariant(
            accent = primaryColor,
            onAccent = ColorPalette.darkStroke,
            foreground = ColorPalette.lightStroke,
            background = ColorPalette.darkStroke,
            surface = ColorPalette.darkStrokeSoftened,
            error = ColorPalette.red,
            onError = ColorPalette.lightStroke,
            light = false,
            warnForeground = ColorPalette.lightWarning,
            dangerForeground = ColorPalette.red,
        )

    val lightColors
        @Composable
        @ReadOnlyComposable
        get() = darkColors.copy(
            foreground = ColorPalette.darkStroke,
            background = ColorPalette.lightStrokeSoftened,
            surface = ColorPalette.lightStroke,
            warnForeground = ColorPalette.darkWarning,
            dangerForeground = ColorPalette.red,
        )

    val colors
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) darkColors else lightColors

    val spacing
        @Composable
        @ReadOnlyComposable
        get() = SpacingVariant(
            gutter = 16.dp,
            content = 8.dp,
            item = 8.dp,
            icon = 8.dp,
            clickSafety = 16.dp,
        )

    val sizing
        @Composable
        @ReadOnlyComposable
        get() = SizingVariant(
            dispayIcon = 120.dp,
            iconButton = 16.dp,
        )

    val shapes
    @Composable
    @ReadOnlyComposable
    get() = ShapeVariant(
        corners = RoundedCornerShape(8.dp),
    )

    val typography
        @Composable
        @ReadOnlyComposable
        get() = TypographyVariant(
            display = TextStyle(
                fontFamily = AprsFonts.titleFont,
                fontWeight = FontWeight.Normal,
                fontSize = 48.sp,
                letterSpacing = 0.25.sp
            ),
            h1 = TextStyle(
                fontFamily = AprsFonts.titleFont,
                fontWeight = FontWeight.Normal,
                fontSize = 34.sp,
                letterSpacing = 0.25.sp
            ),
            h2 = TextStyle(
                fontFamily = AprsFonts.titleFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.sp
            ),
            h3 = TextStyle(
                fontFamily = AprsFonts.titleFont,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                letterSpacing = 0.15.sp
            ),
            body = TextStyle(
                fontFamily = AprsFonts.contentFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            ),
            caption = TextStyle(
                fontFamily = AprsFonts.contentFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 0.4.sp
            ),
            errorCaption = TextStyle(
                fontFamily = AprsFonts.contentFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 0.4.sp,
                color = colors.error
            )
        )
}
