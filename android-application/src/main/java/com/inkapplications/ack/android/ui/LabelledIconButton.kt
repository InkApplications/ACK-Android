package com.inkapplications.ack.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.android.extensions.control.ControlState
import com.inkapplications.android.extensions.control.ControlState.*

@Composable
fun LabelledIconButton(
    icon: ImageVector,
    title: String,
    onClick: (() -> Unit) = {},
    backgroundColor: Color = AckTheme.colors.accent,
    iconColor: Color = AckTheme.colors.onAccent,
    border: BorderStroke? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) = Box(modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = AckTheme.dimensions.clickSafety, horizontal = AckTheme.dimensions.gutter)
    ) {
        Button(
            onClick = onClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
                contentColor = iconColor,
            ),
            border = border,
            enabled = enabled,
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.size(60.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(AckTheme.dimensions.icon),
            )
        }
        Text(
            text = title,
            style = AckTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = AckTheme.dimensions.item)
        )
    }
}
