package com.inkapplications.ack.android.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.symbol.SymbolSelector
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.structures.Symbol

/**
 * Displays a dialog asking the user for a number input.
 *
 * @param title The dialog's title text.
 * @param value The current value of the field.
 * @param validator Asserts that the input value is valid.
 * @param onDismiss Invoked when the user cancels or taps-out of the dialog.
 * @param onSubmit Invoked with the entered validated value when the user saves.
 */
@Composable
fun IntPrompt(
    title: String,
    value: Int,
    validator: Validator<Int>,
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit,
) {
    val inputState = remember { mutableStateOf(value.toString()) }
    val errorState = remember { mutableStateOf<String?>(null) }
    val inputInvalid = stringResource(R.string.prompt_int_invalid_error)
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(shape = AckTheme.shapes.corners) {
            Column(
                modifier = Modifier.padding(AckTheme.spacing.gutter),
            ) {
                Text(title, style = AckTheme.typography.h2)
                TextField(
                    value = inputState.value,
                    onValueChange = { inputState.value = it },
                    isError = errorState.value != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.padding(top = AckTheme.spacing.item).fillMaxWidth(),
                )
                Text(
                    text = errorState.value.orEmpty(),
                    style = AckTheme.typography.errorCaption,
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth().padding(top = AckTheme.spacing.item),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AckTheme.colors.foreground,
                        ),
                        modifier = Modifier.padding(end = AckTheme.spacing.item),
                    ) {
                        Text(stringResource(R.string.prompt_cancel))
                    }
                    TextButton(
                        onClick = {
                            val input = inputState.value.trim().toIntOrNull()
                            if (input == null) {
                                errorState.value = inputInvalid
                            } else {
                                when (val result = validator.validate(input)) {
                                    is ValidationResult.Error -> errorState.value = result.message
                                    ValidationResult.Valid -> onSubmit(input)
                                }
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
