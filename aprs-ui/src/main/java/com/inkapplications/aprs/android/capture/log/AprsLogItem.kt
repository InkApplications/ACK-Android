package com.inkapplications.aprs.android.capture.log

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inkapplications.aprs.android.ui.AprsTheme

@Composable
fun AprsLogItem(
    log: LogItemState,
    onClick: (LogItemState) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(
                vertical = AprsTheme.Spacing.singleItem,
                horizontal = AprsTheme.Spacing.gutter
            )
            .fillMaxWidth()
            .clickable { onClick(log) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(AprsTheme.Spacing.content)
        ) {
            AprsSymbol(log.symbol)

            Column(
                modifier = Modifier.padding(start = AprsTheme.Spacing.content)
            ) {
                Text(
                    text = log.origin,
                    style = AprsTheme.Typography.h3,
                )
                Text(
                    text = log.comment,
                    style = AprsTheme.Typography.body,
                )
            }
        }
    }
}
