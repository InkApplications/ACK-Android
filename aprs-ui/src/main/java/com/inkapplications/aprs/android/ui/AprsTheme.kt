package com.inkapplications.aprs.android.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.inkapplications.aprs.android.R

object AprsTheme {
    object Colors {
        val brand = Color(0xFFFFa72B)
        val brandLight = Color(0xFFFCB652)
        val brandDark = Color(0xFFB16D0D)
        val darkStroke = Color(0xFF212121)
        val darkStroke70 = Color(0x70212121)
        val lightStroke = Color(0xFFFFFFFF)
        val lightStroke70 = Color(0x70FFFFFF)
        val error = Color(0xFFFF432B)
    }
    object Spacing {
        val gutter = 16.dp
        val content = 8.dp
        val item = 8.dp
        val singleItem = item / 2
        val icon = 4.dp
        val clickSafety = 16.dp
    }

    object Typography {
        val h1 @Composable get() = MaterialTheme.typography.h4.copy(
            fontFamily = titleFont,
        )
        val h2 @Composable get() = MaterialTheme.typography.h5.copy(
            fontFamily = titleFont,
        )
        val h3 @Composable get() = MaterialTheme.typography.h6.copy(
            fontFamily = titleFont,
        )
        val body @Composable get() = MaterialTheme.typography.body1.copy(
            fontFamily = contentFont,
        )
        val caption @Composable get() = MaterialTheme.typography.caption.copy(
            fontFamily = titleFont,
        )

        val titleFont = FontFamily(
            Font(R.font.anonymous_pro, FontWeight.Normal),
            Font(R.font.anonymous_pro_bold, FontWeight.Bold),
        )
        val contentFont = FontFamily(
            Font(R.font.lato_regular, FontWeight.Normal),
        )
    }
}

@Composable
fun AprsScreen(content: @Composable () -> Unit) {
    AprsTheme {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        ) {
            content()
        }
    }
}

@Composable
private fun AprsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors(
            primary = AprsTheme.Colors.darkStroke,
            primaryVariant = AprsTheme.Colors.darkStroke,
            secondary = AprsTheme.Colors.brand,
            secondaryVariant = AprsTheme.Colors.brand,
            background = AprsTheme.Colors.darkStroke,
            surface = AprsTheme.Colors.darkStroke,
            error = AprsTheme.Colors.error,
            onPrimary = AprsTheme.Colors.lightStroke,
            onSecondary = AprsTheme.Colors.darkStroke,
            onBackground = AprsTheme.Colors.lightStroke,
            onSurface = AprsTheme.Colors.lightStroke,
            onError = AprsTheme.Colors.lightStroke,
        ) else lightColors(
            primary = AprsTheme.Colors.lightStroke,
            primaryVariant = AprsTheme.Colors.lightStroke,
            secondary = AprsTheme.Colors.brand,
            secondaryVariant = AprsTheme.Colors.brand,
            background = AprsTheme.Colors.lightStroke,
            surface = AprsTheme.Colors.lightStroke,
            error = AprsTheme.Colors.error,
            onPrimary = AprsTheme.Colors.darkStroke,
            onSecondary = AprsTheme.Colors.darkStroke,
            onBackground = AprsTheme.Colors.darkStroke,
            onSurface = AprsTheme.Colors.darkStroke,
            onError = AprsTheme.Colors.lightStroke,
        ),
        typography = MaterialTheme.typography.copy(
            body1 = AprsTheme.Typography.body,
            body2 = AprsTheme.Typography.body.copy(fontWeight = FontWeight.Bold),
            h1 = MaterialTheme.typography.h4,
            h2 = MaterialTheme.typography.h5,
            h3 = MaterialTheme.typography.h6,
            h4 = AprsTheme.Typography.h1,
            h5 = AprsTheme.Typography.h2,
            h6 = AprsTheme.Typography.h3,
        ),
    ) { content() }
}
