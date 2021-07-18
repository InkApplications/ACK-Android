package com.inkapplications.aprs.android.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.inkapplications.aprs.android.ui.AprsTheme

@Composable
fun IntStateRow(
    state: SettingState.IntState,
    onClick: () -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .clickable(onClick = onClick)
        .padding(vertical = AprsTheme.Spacing.clickSafety, horizontal = AprsTheme.Spacing.gutter)
) {
    Text(state.name, fontWeight = FontWeight.Bold)
    Spacer(Modifier.weight(1f))
    Text(state.value.toString(), style = AprsTheme.Typography.caption)
}

@Composable
fun StringStateRow(
    state: SettingState.StringState,
    onClick: () -> Unit
) = Column(
    modifier = Modifier
        .clickable(onClick = onClick)
        .fillMaxWidth()
        .padding(vertical = AprsTheme.Spacing.clickSafety, horizontal = AprsTheme.Spacing.gutter)
) {
    Text(state.name, fontWeight = FontWeight.Bold)
    Text(state.value, style = AprsTheme.Typography.caption)
}

@Composable
fun BooleanStateRow(
    state: SettingState.BooleanState,
    onChange: (Boolean) -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
        .clickable { onChange(!state.value) }
        .padding(vertical = AprsTheme.Spacing.clickSafety, horizontal = AprsTheme.Spacing.gutter)
) {
    Text(state.name, fontWeight = FontWeight.Bold)
    Spacer(Modifier.weight(1f))
    Switch(
        checked = state.value,
        onCheckedChange = { checked ->
            onChange(checked)
        }
    )
}
