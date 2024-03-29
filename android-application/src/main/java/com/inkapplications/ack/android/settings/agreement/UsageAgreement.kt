package com.inkapplications.ack.android.settings.agreement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun UsageAgreement() {
    Column {
        Text(
            text = stringResource(R.string.usage_legal_services_title),
            style = AckTheme.typography.h2,
        )
        Spacer(Modifier.height(AckTheme.spacing.item))
        Text(
            text = stringResource(R.string.usage_legal_services_section1),
        )
        Text(
            text = stringResource(R.string.usage_legal_services_section2),
        )

        Spacer(Modifier.height(AckTheme.spacing.content))

        Text(
            text = stringResource(R.string.usage_data_privacy_title),
            style = AckTheme.typography.h2,
        )
        Spacer(Modifier.height(AckTheme.spacing.item))
        Text(
            text = stringResource(R.string.usage_data_privacy_section1),
        )
        Spacer(Modifier.height(AckTheme.spacing.item))
        Text(
            text = stringResource(R.string.usage_data_privacy_section2),
        )

        Spacer(Modifier.height(AckTheme.spacing.content))

        Text(
            text = stringResource(R.string.usage_warranty_title),
            style = AckTheme.typography.h2,
        )
        Spacer(Modifier.height(AckTheme.spacing.item))
        Text(
            text = stringResource(R.string.usage_warranty_section1),
        )

        Spacer(Modifier.height(AckTheme.spacing.content))

        Text(
            text = stringResource(R.string.usage_rights_title),
            style = AckTheme.typography.h2,
        )
        Spacer(Modifier.height(AckTheme.spacing.item))
        Text(
            text = stringResource(R.string.usage_rights_section1),
        )
        Text(
            text = stringResource(R.string.usage_rights_section2),
        )
        Text(
            text = stringResource(R.string.usage_rights_section3),
        )
        Text(
            text = stringResource(R.string.usage_rights_section4),
        )
    }
}
