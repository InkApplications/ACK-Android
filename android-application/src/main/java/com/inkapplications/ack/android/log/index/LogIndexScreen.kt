package com.inkapplications.ack.android.log.index

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.AprsLogItem
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

/**
 * Screen that displays a scrolling list of log items.
 */
@Composable
fun LogIndexScreen(
    viewModel: LogIndexViewModel = hiltViewModel(),
    controller: LogIndexController,
) = AckScreen {
    val state = viewModel.indexState.collectAsState()

    when (val currentState = state.value) {
        LogIndexState.Initial -> {}
        is LogIndexState.LogList -> LogList(currentState, controller)
        is LogIndexState.Empty -> EmptyState()
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(bottom = AckTheme.spacing.navigationProtection)
            .fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Summarize,
            contentDescription = null,
            tint = AckTheme.colors.foregroundInactive,
            modifier = Modifier.size(AckTheme.sizing.dispayIcon),
        )
        Text(stringResource(R.string.log_index_empty))
    }
}

@Composable
private fun LogList(
    state: LogIndexState.LogList,
    controller: LogIndexController,
) {
    LazyColumn(
        contentPadding = PaddingValues(top = AckTheme.spacing.gutter, bottom = AckTheme.spacing.navigationProtection)
    ) {
        items(state.logs) { log ->
            AprsLogItem(log, controller::onLogListItemClick)
        }
    }
}
