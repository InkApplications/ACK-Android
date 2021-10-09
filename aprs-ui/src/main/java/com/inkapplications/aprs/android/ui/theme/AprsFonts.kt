package com.inkapplications.aprs.android.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.inkapplications.aprs.android.R

object AprsFonts {
    val titleFont = FontFamily(
        Font(R.font.anonymous_pro, FontWeight.Normal),
        Font(R.font.anonymous_pro_bold, FontWeight.Bold),
    )
    val contentFont = FontFamily(
        Font(R.font.lato_regular, FontWeight.Normal),
    )
}
