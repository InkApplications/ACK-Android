package com.inkapplications.ack.android.capture.log

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
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun AprsLogItem(
    log: LogItemViewModel,
    onClick: (LogItemViewModel) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(
                vertical = AckTheme.dimensions.singleItem,
                horizontal = AckTheme.dimensions.gutter
            )
            .fillMaxWidth()
            .clickable { onClick(log) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(AckTheme.dimensions.content)
        ) {
            AprsSymbol(log.symbol)

            Column(
                modifier = Modifier.padding(start = AckTheme.dimensions.content)
            ) {
                Text(
                    text = log.origin,
                    style = AckTheme.typography.h3,
                )
                Text(
                    text = log.comment,
                    style = AckTheme.typography.body,
                )
            }
        }
    }
}
