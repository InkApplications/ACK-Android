package com.inkapplications.ack.android.symbol

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.code
import com.inkapplications.ack.structures.symbolOf

/**
 * Selection UI showing a list of APRS symbols to select.
 *
 * This invokes the [onChange] callback any time a symbol is clicked on
 * or a valid symbol is typed into the text field.
 */
@Composable
fun SymbolSelector(
    onChange: (Symbol?) -> Unit,
    modifier: Modifier = Modifier,
    default: Symbol? = null,
    viewModel: SymbolSelectorViewModel = hiltViewModel(),
) {
    val current = remember { mutableStateOf(default?.code) }

    Column(modifier = modifier) {
        TextField(
            value = current.value.orEmpty(),
            leadingIcon = {
                val icon = current.value
                    .takeIf { it?.length == 2 }
                    ?.let {
                        try { symbolOf(it[0], it[1]) }
                        catch (e: IllegalArgumentException) { null }
                    }
                    ?.let { viewModel.createBitmap(it) }
                    ?: return@TextField
                Image(
                    bitmap = icon.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(24.dp + AckTheme.spacing.icon)
                        .height(24.dp + AckTheme.spacing.icon)
                        .padding(AckTheme.spacing.icon),
                )
            },
            onValueChange = { new ->
                if (new.length > 2) return@TextField
                current.value = new
                runCatching { symbolOf(new) }.getOrNull()?.run(onChange)
            },
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth()
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 32.dp),
        ) {
            items(viewModel.symbols) { symbol ->
                val bitmap = viewModel.createBitmap(symbol)
                IconButton(onClick = {
                    current.value = symbol.code
                    onChange(symbol)
                }) {
                    if (bitmap == null) {
                        return@IconButton
                    }
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .width(24.dp + AckTheme.spacing.icon)
                            .height(24.dp + AckTheme.spacing.icon)
                            .padding(AckTheme.spacing.icon),
                    )
                }
            }
        }
    }
}
