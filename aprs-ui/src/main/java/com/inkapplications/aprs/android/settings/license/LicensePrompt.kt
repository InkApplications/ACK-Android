package com.inkapplications.aprs.android.settings.license

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.inkapplications.aprs.android.onboard.LicensePromptFieldValues
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme

@Composable
fun LicensePromptScreen(
    initialValues: LicensePromptFieldValues,
    validator: LicensePromptValidator,
    onContinue: (LicensePromptFieldValues) -> Unit,
) = AprsScreen {
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

        val callsign = rememberSaveable { mutableStateOf(initialValues.callsign) }
        val passcode = rememberSaveable { mutableStateOf(initialValues.passcode) }
        val callsignError = rememberSaveable { mutableStateOf<String?>(null) }
        val passcodeError = rememberSaveable { mutableStateOf<String?>(null) }

        TextField(
            value = callsign.value,
            label = { Text("Callsign") },
            onValueChange = { callsign.value = it; passcode.value = "" },
            isError = callsignError.value != null,
            modifier = Modifier.fillMaxWidth(),
        )
        if (callsignError.value != null) Text(callsignError.value.orEmpty(), style = AprsTheme.Typography.errorCaption)
        Spacer(Modifier.height(AprsTheme.Spacing.item))
        TextField(
            value = passcode.value,
            label = { Text("APRS-IS Passcode") },
            onValueChange = { passcode.value = it },
            enabled = callsign.value.isNotBlank(),
            isError = passcodeError.value != null,
            modifier = Modifier.fillMaxWidth(),
        )
        if (passcodeError.value != null) Text(passcodeError.value.orEmpty(), style = AprsTheme.Typography.errorCaption)
        Spacer(Modifier.weight(1f))
        Button(
            onClick = {
                callsignError.value = validator.getLicenseError(callsign.value)
                passcodeError.value = validator.getPasscodeError(callsign.value, passcode.value)
                if (validator.isValid(callsign.value, passcode.value)) {
                    onContinue(LicensePromptFieldValues(callsign.value, passcode.value))
                }
            },
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
