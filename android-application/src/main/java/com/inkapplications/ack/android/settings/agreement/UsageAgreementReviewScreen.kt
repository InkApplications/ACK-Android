package com.inkapplications.ack.android.settings.agreement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.NavigationRow
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun UsageAgreementReviewScreen(
    onBackPressed: () -> Unit,
) = AckScreen {
    Column {
        NavigationRow(
            title = stringResource(R.string.usage_title),
            onBackPressed = onBackPressed,
        )
        Column(Modifier.verticalScroll(rememberScrollState()).fillMaxHeight().padding(AckTheme.dimensions.gutter)) {
            UsageAgreement()
        }
    }
}
