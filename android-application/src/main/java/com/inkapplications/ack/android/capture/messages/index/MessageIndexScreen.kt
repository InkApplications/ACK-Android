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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.messages.index.ConversationItemViewModel
import com.inkapplications.ack.android.capture.messages.index.MessageIndexScreenState
import com.inkapplications.ack.android.capture.messages.index.MessagesScreenController
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun MessageIndexScreen(
    screenState: State<MessageIndexScreenState>,
    controller: MessagesScreenController,
    bottomProtection: Dp,
    bottomContentProtection: Dp,
) = AckScreen {
    when (val state = screenState.value) {
        is MessageIndexScreenState.ConversationList -> ConversationList(state, controller, bottomContentProtection)
        is MessageIndexScreenState.Empty -> EmptyPlaceholder()
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = bottomProtection),
        contentAlignment = Alignment.BottomEnd,
    ) {
        FloatingActionButton(
            onClick = controller::onCreateMessageClick,
            backgroundColor = AckTheme.colors.surface,
            contentColor = contentColorFor(AckTheme.colors.surface),
            modifier = Modifier.padding(AckTheme.dimensions.gutter)
        ) {
            Icon(Icons.Default.Message, stringResource(R.string.messages_compose_action))
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
private fun ConversationList(
    state: MessageIndexScreenState.ConversationList,
    controller: MessagesScreenController,
    bottomProtection: Dp,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = bottomProtection)
    ) {
        items(state.conversations) { conversation ->
            ConversationItem(conversation, controller)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ConversationItem(viewModel: ConversationItemViewModel, controller: MessagesScreenController) {
    Card(
        onClick = { controller.onConversationClick(viewModel.correspondent) },
        modifier = Modifier.padding(horizontal = AckTheme.dimensions.gutter, vertical = AckTheme.dimensions.singleItem)
    ) {
        Column(modifier = Modifier.padding(AckTheme.dimensions.content).fillMaxWidth()) {
            Text(viewModel.name, style = AckTheme.typography.h2)
            Text(viewModel.messagePreview)
        }
    }
}
