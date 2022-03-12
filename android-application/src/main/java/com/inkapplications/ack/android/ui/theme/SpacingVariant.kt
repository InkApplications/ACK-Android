package com.inkapplications.ack.android.ui.theme

import androidx.compose.ui.unit.Dp

data class SpacingVariant(
    val gutter: Dp,
    val content: Dp,
    val item: Dp,
    val icon: Dp,
    val clickSafety: Dp,
    val placeholderIcon: Dp,
    val navigationProtection: Dp,
) {
    val singleItem: Dp = item / 2
}
