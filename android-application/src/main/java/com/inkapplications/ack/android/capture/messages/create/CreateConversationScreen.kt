package com.inkapplications.ack.android.capture.messages.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun CreateConversationScreen(
    controller: CreateConversationController,
) {
    AckScreen {
        Column(
            modifier = Modifier.padding(AckTheme.dimensions.gutter)
        ) {
            Text(stringResource(R.string.messages_create_title), style = AckTheme.typography.h1)

            val callsign = rememberSaveable { mutableStateOf("") }
            TextField(
                value = callsign.value,
                label = { Text(stringResource(R.string.messages_create_field_callsign_label)) },
                onValueChange = { callsign.value = it },
                modifier = Modifier.fillMaxWidth().padding(top = AckTheme.dimensions.content),
            )

            Button(
                onClick = { controller.onCreateClick(callsign.value.trim()) },
                modifier = Modifier.fillMaxWidth().padding(top = AckTheme.dimensions.item),
            ) {
                Text(stringResource(R.string.messages_create_start_action, callsign.value))
            }
        }
    }
}
