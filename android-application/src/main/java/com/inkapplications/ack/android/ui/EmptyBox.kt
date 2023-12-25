package com.inkapplications.ack.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.inkapplications.ack.android.ui.theme.AckTheme

/**
 * Placeholder with an icon and caption to explain that there is no data loaded
 * for this section of the app yet.
 *
 * @param icon A large icon to place in the middle of the screen
 * @param caption A snippet of text to explain what data is missing.
 */
@Composable
fun EmptyBox(
    icon: ImageVector,
    caption: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AckTheme.colors.foregroundInactive,
            modifier = Modifier.size(AckTheme.sizing.dispayIcon),
        )
        Text(caption)
    }
}
