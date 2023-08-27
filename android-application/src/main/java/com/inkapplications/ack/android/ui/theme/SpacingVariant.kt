package com.inkapplications.ack.android.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SpacingVariant(
    val gutter: Dp,
    val content: Dp,
    val item: Dp,
    val icon: Dp,
    val clickSafety: Dp,
    val bottomBarHeight: Dp = 60.dp,
    val fabSize: Dp = 40.dp,
) {
    val navigationProtection = bottomBarHeight + (fabSize / 2) + content
    val singleItem: Dp = item / 2
}
