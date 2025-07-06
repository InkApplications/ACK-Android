package com.inkapplications.ack.android.onboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.settings.agreement.UsageAgreement
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun UsageAgreementPrompt(
    controller: UserAgreementController,
) {
    Column(
        modifier = Modifier
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
            .padding(AckTheme.spacing.gutter)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            stringResource(R.string.usage_title),
            style = AckTheme.typography.h1,
        )
        Spacer(Modifier.height(AckTheme.spacing.content))
        UsageAgreement()
        Spacer(Modifier.weight(1f).defaultMinSize(minHeight = AckTheme.spacing.content))
        Button(
            onClick = controller::onTermsDeclineClick,
            colors = ButtonDefaults.outlinedButtonColors(),
            modifier = Modifier.fillMaxWidth().padding(top = AckTheme.spacing.content),
        ) {
            Text(stringResource(R.string.usage_decline_action))
        }
        Spacer(Modifier.height(AckTheme.spacing.item))
        Button(
            onClick = controller::onTermsAgreeClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.usage_agree_action))
        }
    }
}
