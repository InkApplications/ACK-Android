package com.inkapplications.aprs.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inkapplications.aprs.android.ui.theme.AprsTheme

@Composable
fun NavigationRow(
    title: String,
    onBackPressed: (() -> Unit)? = null,
) = NavigationRow(
    title = {
        Text(
            text = title,
            style = AprsTheme.typography.h1,
            modifier = Modifier.padding(
                start = AprsTheme.spacing.item,
            )
        )
    },
    onBackPressed = onBackPressed,
)

@Composable
fun NavigationRow(
    title: @Composable () -> Unit,
    onBackPressed: (() -> Unit)? = null,
) = Row (
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(
        start = 0.dp,
        top = AprsTheme.spacing.gutter,
        end = AprsTheme.spacing.gutter,
        bottom = AprsTheme.spacing.content,
    )
) {
    if (onBackPressed != null) {
        IconButton(
            onClick = { onBackPressed() },
            Modifier.padding(start = 0.dp)
        ) {
            Icon(Icons.Default.ArrowBack, "")
        }
    } else {
        Spacer(Modifier.width(AprsTheme.spacing.item))
    }
    title()
}
