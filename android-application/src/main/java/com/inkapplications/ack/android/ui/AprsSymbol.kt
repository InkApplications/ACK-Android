package com.inkapplications.ack.android.capture.log

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun AprsSymbol(symbol: Bitmap?) {
    if (symbol != null) {
        Image(
            bitmap = symbol.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .width(24.dp + AckTheme.spacing.icon)
                .height(24.dp + AckTheme.spacing.icon)
                .padding(AckTheme.spacing.icon)
        )
    } else {
        Box(
            modifier = Modifier.width(24.dp).height(24.dp).padding(AckTheme.spacing.icon)
        ) {}
    }
}
