package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.EmptyBox
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun ConversationScreen(
    controller: ConversationController,
    viewModel: ConversationViewModel = hiltViewModel(),
) = AckScreen {
    Column {
        val viewState = viewModel.conversationState.collectAsState().value
        TopAppBar {
            IconButton(
                onClick = controller::onNavigateUpPressed,
            ) {
                Icon(Icons.Default.ArrowBack, stringResource(R.string.navigate_up))
            }
            Text(viewState.title, style = AckTheme.typography.h1)
        }
        when (viewState) {
            is ConversationViewState.Initial -> {}
            is ConversationViewState.Empty -> EmptyBox(
                icon = Icons.Default.Message,
                caption = stringResource(R.string.messages_conversation_empty),
                modifier = Modifier.weight(1f),
            )
            is ConversationViewState.MessageList -> LazyColumn(
                contentPadding = PaddingValues(vertical = AckTheme.spacing.gutter),
                reverseLayout = true,
                modifier = Modifier.weight(1f)
            ) {
                items(viewState.messages.reversed()) { IncomingMessage(it) }
            }
        }
        Surface(
            shape = AckTheme.shapes.corners,
            modifier = Modifier
                .fillMaxWidth()
                .padding(AckTheme.spacing.gutter),
        ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
            ) {
                val textFieldValue = remember { mutableStateOf("") }
                OutlinedTextField(
                    value = textFieldValue.value,
                    onValueChange = { textFieldValue.value = it },
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.messages_conversation_send_placeholder)) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                    ),
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        controller.onSendMessage(textFieldValue.value)
                        textFieldValue.value = ""
                    },
                ) {
                    Icon(Icons.Default.Send, stringResource(R.string.messages_conversation_send_action))
                }
            }
        }
    }
}

@Composable
private fun IncomingMessage(viewModel: MessageItemState) {
    Box(
        contentAlignment = viewModel.alignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AckTheme.spacing.gutter, vertical = AckTheme.spacing.singleItem)
    ) {
        Message(viewModel)
    }
}

@Composable
private fun Message(viewModel: MessageItemState) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.defaultMinSize(minWidth = 75.dp)
    ) {
        Column(
            modifier = Modifier.padding(AckTheme.spacing.content)
        ) {
            Text(viewModel.message, style = AckTheme.typography.body)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(
                    text = viewModel.timestamp,
                    style = AckTheme.typography.caption,
                    modifier = Modifier.padding(end = AckTheme.spacing.item),
                )
                Icon(
                    imageVector = viewModel.icon,
                    contentDescription = viewModel.iconDescription,
                    modifier = Modifier.size(10.dp),
                )
            }
        }
    }
}
