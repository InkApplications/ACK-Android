package com.inkapplications.ack.android.symbol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.structures.Symbol

/**
 * Input prompt allowing the user to select a symbol.
 */
@Composable
fun SymbolPrompt(
    title: String,
    value: Symbol?,
    onDismiss: () -> Unit,
    onSubmit: (Symbol?) -> Unit,
) {
    val currentSymbol = remember { mutableStateOf(value) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = AckTheme.shapes.corners,
        ) {
            Column(
                modifier = Modifier.padding(AckTheme.spacing.gutter),
            ) {
                Text(title, style = AckTheme.typography.h2)
                SymbolSelector(
                    onChange = {
                        currentSymbol.value = it
                    },
                    default = currentSymbol.value,
                    modifier = Modifier.padding(vertical = AckTheme.spacing.item)
                        .align(Alignment.CenterHorizontally)
                        .sizeIn(maxWidth = 400.dp, maxHeight = 200.dp)
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
                            onSubmit(currentSymbol.value)
                        },
                    ) {
                        Text(stringResource(R.string.prompt_save))
                    }
                }
            }
        }
    }
}
