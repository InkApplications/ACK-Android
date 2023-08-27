package com.inkapplications.ack.android.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme

/**
 * Clickable row with a selection state.
 *
 * This is designed to be used in a selection screen or pop up, to indicate
 * a list of options with one or more items in a "selected" state.
 *
 * @param name User-readable string displayed in this row.
 * @param selected Whether to indicate the row as currently selected/enabled.
 * @param onClick Invoked when the user clicks the row.
 */
@Composable
fun SelectionRow(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = AckTheme.spacing.clickSafety, horizontal = AckTheme.spacing.gutter),
    ) {
        Text(name, style = AckTheme.typography.h2)

        if (selected) {
            Icon(
                Icons.Default.Check,
                contentDescription = stringResource(id = R.string.state_selected_description),
            )
        }
    }
}
