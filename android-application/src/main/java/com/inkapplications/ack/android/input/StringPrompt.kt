package com.inkapplications.ack.android.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme

/**
 * Displays a dialog asking the user for an arbitrary single-line string input.
 *
 * @param title The dialog's title text.
 * @param value The current value of the field.
 * @param validator Asserts that the input value is valid.
 * @param onDismiss Invoked when the user cancels or taps-out of the dialog.
 * @param onSubmit Invoked with the entered validated value when the user saves.
 */
@Composable
fun StringPrompt(
    title: String,
    value: String,
    validator: Validator<String>,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    val inputState = remember { mutableStateOf(value) }
    val errorState = remember { mutableStateOf<String?>(null) }
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(shape = AckTheme.shapes.corners) {
            Column(
                modifier = Modifier.padding(AckTheme.dimensions.gutter),
            ) {
                Text(title, style = AckTheme.typography.h2)
                TextField(
                    value = inputState.value,
                    onValueChange = { inputState.value = it },
                    isError = errorState.value != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    modifier = Modifier.padding(top = AckTheme.dimensions.item).fillMaxWidth(),
                )
                Text(
                    text = errorState.value.orEmpty(),
                    style = AckTheme.typography.errorCaption,
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(top = AckTheme.dimensions.item),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AckTheme.colors.foreground,
                        ),
                        modifier = Modifier.padding(end = AckTheme.dimensions.item),
                    ) {
                        Text(stringResource(R.string.prompt_cancel))
                    }
                    TextButton(
                        onClick = {
                            val input = inputState.value.trim()
                            when (val result = validator.validate(input)) {
                                is ValidationResult.Error -> errorState.value = result.message
                                ValidationResult.Valid -> onSubmit(input)
                            }
                        },
                    ) {
                        Text(stringResource(R.string.prompt_save))
                    }
                }
            }
        }
    }
}
