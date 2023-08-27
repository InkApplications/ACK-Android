package com.inkapplications.ack.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.ui.theme.AckTheme

/**
 * Rounded UI element, similar to a button, but intended to also display state.
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun AckChip(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = BorderStroke(1.dp, AckTheme.colors.foregroundInactive),
    content: @Composable () -> Unit,
) {
    if (onClick != null) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            elevation = 1.dp,
            border = border,
            onClick = onClick,
            modifier = modifier,
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(AckTheme.spacing.content),
            ) { content() }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(24.dp),
            elevation = 1.dp,
            border = border,
            modifier = modifier,
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(AckTheme.spacing.content),
            ) { content() }
        }
    }
}
