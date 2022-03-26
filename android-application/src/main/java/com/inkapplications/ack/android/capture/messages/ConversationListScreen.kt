package com.inkapplications.ack.android.capture.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Message
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun MessagesScreen(
    screenState: State<MessageScreenState>,
    controller: MessagesScreenController,
    bottomContentProtection: Dp,
) = AckScreen {
    when (val state = screenState.value) {
        is MessageScreenState.ConversationList -> ConversationList(state, controller)
        is MessageScreenState.Empty -> EmptyPlaceholder()
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = bottomContentProtection),
        contentAlignment = Alignment.BottomEnd,
    ) {
        FloatingActionButton(
            onClick = controller::onCreateMessageClick,
            backgroundColor = AckTheme.colors.surface,
            contentColor = contentColorFor(AckTheme.colors.surface),
            modifier = Modifier.padding(AckTheme.dimensions.gutter)
        ) {
            Icon(Icons.Default.Message, "Compose Message")
        }
    }
}

@Composable
private fun EmptyPlaceholder() = Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.fillMaxSize()
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            tint = AckTheme.colors.foregroundInactive,
            modifier = Modifier.size(AckTheme.dimensions.placeholderIcon),
        )
        Text("No messages received")
    }
}

@Composable
private fun ConversationList(state: MessageScreenState.ConversationList, controller: MessagesScreenController) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = AckTheme.dimensions.navigationProtection)
    ) {
        items(state.conversations) { conversation ->
            ConversationItem(conversation, controller)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ConversationItem(viewModel: ConversationViewModel, controller: MessagesScreenController) {
    Card(
        onClick = { controller.onConversationClick(viewModel.idCallsign) },
        modifier = Modifier.padding(horizontal = AckTheme.dimensions.gutter, vertical = AckTheme.dimensions.singleItem)
    ) {
        Column(modifier = Modifier.padding(AckTheme.dimensions.content).fillMaxWidth()) {
            Text(viewModel.name, style = AckTheme.typography.h2)
            Text(viewModel.messagePreview)
        }
    }
}
