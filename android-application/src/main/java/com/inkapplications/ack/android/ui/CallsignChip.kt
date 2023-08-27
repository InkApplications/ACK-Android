package com.inkapplications.ack.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun CallsignChip(
    callsign: String,
    verified: Boolean,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = null,
) {
    if (onClick != null) {
        AckChip(
            border = border,
            onClick = onClick,
        ) {
            CallsignChipContent(callsign, verified)
        }
    } else {
        AckChip(
            border = border,
        ) {
            CallsignChipContent(callsign, verified)
        }
    }
}

@Composable
private fun CallsignChipContent(
    callsign: String,
    verified: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (verified) Icon(
            Icons.Default.Verified,
            stringResource(R.string.settings_callsign_verified_description),
            modifier = Modifier.padding(end = AckTheme.spacing.icon)
        )
        Text(callsign, style = AckTheme.typography.h2)
    }
}
