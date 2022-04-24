package com.inkapplications.ack.android.capture.messages.conversation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.R
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
                Icon(Icons.Default.ArrowBack, stringResource(R.string.navigate_up))
            }
            Text(viewState.title, style = AckTheme.typography.h1)
        }
        when (viewState) {
            is ConverstationViewState.MessageList -> LazyColumn(
                contentPadding = PaddingValues(vertical = AckTheme.dimensions.gutter),
                reverseLayout = true,
                modifier = Modifier.weight(1f)
            ) {
                items(viewState.messages.reversed()) { IncomingMessage(it) }
            }
        }
        Surface(
            shape = AckTheme.shapes.corners,
            modifier = Modifier.fillMaxWidth().padding(AckTheme.dimensions.gutter),
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
