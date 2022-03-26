package com.inkapplications.ack.android.capture.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun ConversationScreen(
    viewState: ConverstationViewState,
    controller: ConversationController,
) = AckScreen {
    Column {
        TopAppBar {
            IconButton(
                onClick = controller::onNavigateUpPressed,
            ) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text(viewState.title, style = AckTheme.typography.h1)
        }
        when (viewState) {
            is ConverstationViewState.MessageList -> LazyColumn(
                contentPadding = PaddingValues(vertical = AckTheme.dimensions.gutter),
            ) {
                items(viewState.messages) { IncomingMessage(it) }
            }
        }
    }
}

@Composable
private fun IncomingMessage(viewModel: MessageItemViewModel) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.padding(horizontal = AckTheme.dimensions.gutter, vertical = AckTheme.dimensions.singleItem)
    ) {
        Message(viewModel)
    }
}

@Composable
private fun Message(viewModel: MessageItemViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(AckTheme.dimensions.content)
        ) {
            Text(viewModel.message, style = AckTheme.typography.body)
            Text(
                text = viewModel.timestamp,
                style = AckTheme.typography.caption,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}
