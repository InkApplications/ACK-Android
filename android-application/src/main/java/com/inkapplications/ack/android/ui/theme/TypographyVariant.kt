package com.inkapplications.ack.android.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

data class TypographyVariant(
    val display: TextStyle,
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val body: TextStyle,
    val caption: TextStyle,
    val errorCaption: TextStyle,
) {
    val material = Typography(
        defaultFontFamily = AprsFonts.contentFont,
        h1 = h1,
        h2 = h2,
        h3 = h3,
        h4 = h1,
        h5 = h2,
        h6 = h3,
        subtitle1 = caption,
        subtitle2 = caption,
        body1 = body,
        body2 = body.copy(fontWeight = FontWeight.Bold),
        button = body,
        caption = caption,
        overline = body,
    )
}
