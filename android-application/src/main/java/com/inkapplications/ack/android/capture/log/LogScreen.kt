package com.inkapplications.ack.android.capture.log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun LogScreen(
    state: State<LogScreenState>,
    controller: LogScreenController,
) = AckScreen {
    when (val currentState = state.value) {
        is LogScreenState.LogList -> LogList(currentState, controller)
        is LogScreenState.Empty -> EmptyState()
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = AckTheme.dimensions.navigationProtection).fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Summarize,
            contentDescription = null,
            tint = AckTheme.colors.foregroundInactive,
            modifier = Modifier.size(AckTheme.dimensions.placeholderIcon),
        )
        Text(stringResource(R.string.capture_log_empty))
    }
}

@Composable
private fun LogList(
    state: LogScreenState.LogList,
    controller: LogScreenController,
) {
    LazyColumn(
        contentPadding = PaddingValues(top = AckTheme.dimensions.gutter, bottom = AckTheme.dimensions.navigationProtection)
    ) {
        items(state.logs) { log ->
            AprsLogItem(log, controller::onLogListItemClick)
        }
    }
}
