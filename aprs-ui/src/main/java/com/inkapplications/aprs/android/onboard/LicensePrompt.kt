package com.inkapplications.aprs.android.onboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.inkapplications.aprs.android.ui.AprsTheme

@Composable
fun LicensePrompt(
    onContinue: (String, String) -> Unit,
) {
    Column (
        modifier = Modifier.padding(AprsTheme.Spacing.gutter),
    ) {
        Text(
            "License Info",
            style = AprsTheme.Typography.h1,
        )
        Spacer(Modifier.height(AprsTheme.Spacing.item))
        Text("If you have a radio license, enter the information below to enable transmit and internet services.")
        Spacer(Modifier.height(AprsTheme.Spacing.content))
        val callsign = remember { mutableStateOf("") }
        val passcode = remember { mutableStateOf("") }
        TextField(
            value = callsign.value,
            label = { Text("Callsign") },
            onValueChange = { callsign.value = it },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(AprsTheme.Spacing.item))
        TextField(
            value = passcode.value,
            label = { Text("APRS-IS Passcode") },
            onValueChange = { passcode.value = it },
            enabled = callsign.value.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.weight(1f))
        Button(
            onClick = { onContinue(callsign.value, passcode.value) },
            modifier = Modifier.fillMaxWidth().padding(top = AprsTheme.Spacing.item),
        ) {
            if (callsign.value.isBlank() && passcode.value.isBlank()) {
                Text("Skip")
            } else {
                Text("Continue")
            }
        }
    }
}
