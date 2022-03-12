package com.inkapplications.ack.android.capture.messages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AprsTheme
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.structures.PacketData

@Composable
fun MessageScreen(
    screenState: State<MessageScreenState>,
    controller: MessageScreenController,
) = AckScreen {
    when (val state = screenState.value) {
        is MessageScreenState.MessageList -> MessageList(state)
        is MessageScreenState.Empty -> EmptyPlaceholder()
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
            tint = AprsTheme.colors.foregroundInactive,
            modifier = Modifier.size(AprsTheme.spacing.placeholderIcon),
        )
        Text("No messages received")
    }
}

@Composable
private fun MessageList(state: MessageScreenState.MessageList) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = AprsTheme.spacing.navigationProtection)
    ) {
        items(state.messages) { message ->
            MessageItem(message)
        }
    }
}

@Composable
private fun MessageItem(packet: CapturedPacket) {
    Card(modifier = Modifier.padding(horizontal = AprsTheme.spacing.gutter, vertical = AprsTheme.spacing.singleItem)) {
        Column(modifier = Modifier.padding(AprsTheme.spacing.content).fillMaxWidth()) {
            Text(packet.parsed.route.source.toString(), style = AprsTheme.typography.h2)
            val data = packet.parsed.data as? PacketData.Message ?: run {
                Text("Unsupported data type: ${packet.parsed.data.javaClass.simpleName}")
                return@Card
            }
            Text(data.message)
            if (data.messageNumber != null) {
                Text("#${data.messageNumber}")
            }
        }
    }
}
