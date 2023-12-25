package com.inkapplications.ack.android.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.IntPrompt
import com.inkapplications.ack.android.input.StringPrompt
import com.inkapplications.ack.android.settings.buildinfo.BuildInfo
import com.inkapplications.ack.android.symbol.SymbolPrompt
import com.inkapplications.ack.android.ui.CallsignChip
import com.inkapplications.ack.android.ui.NavigationRow
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.structures.Symbol
import com.inkapplications.ack.structures.code

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    controller: SettingsController,
) = AckScreen {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        NavigationRow(
            title = stringResource(R.string.settings_title),
            onBackPressed = controller::onBackPressed,
        )
        LicenseRow(viewModel.licenseState.collectAsState(), controller)
        val promptSetting = remember { mutableStateOf<SettingState?>(null) }
        val settingState = promptSetting.value
        when {
            settingState is SettingState.StringState && settingState.setting is StringBackedSetting<*> && settingState.setting.defaultData is Symbol -> {
                val symbolSetting = settingState.setting as StringBackedSetting<Symbol>
                SymbolPrompt(
                    title = symbolSetting.name,
                    value = symbolSetting.storageTransformer.toData(settingState.value),
                    onDismiss = { promptSetting.value = null },
                    onSubmit = {
                        if (it != null) {
                            controller.onStringSettingChanged(settingState, it.code)
                        }
                        promptSetting.value = null
                    }
                )
            }
            settingState is SettingState.IntState -> IntPrompt(
                title = settingState.setting.name,
                value = settingState.value,
                validator = settingState.setting.validator,
                onDismiss = { promptSetting.value = null },
                onSubmit = {
                    controller.onIntSettingChanged(settingState, it)
                    promptSetting.value = null
                }
            )
            settingState is SettingState.StringState -> StringPrompt(
                title = settingState.setting.name,
                value = settingState.value,
                validator = settingState.setting.validator,
                onDismiss = { promptSetting.value = null },
                onSubmit = {
                    controller.onStringSettingChanged(settingState, it)
                    promptSetting.value = null
                }
            )
            else -> {}
        }

        when (val listState = viewModel.settingsList.collectAsState().value) {
            SettingsListViewState.Initial -> {}
            is SettingsListViewState.Loaded -> {
                listState.settingsList.forEach {
                    SettingsCard(group = it, controller = controller, promptSetting = promptSetting)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.settings_advanced_toggle_label),
                        style = AckTheme.typography.caption,
                        modifier = Modifier.padding(end = AckTheme.spacing.item)
                    )
                    Switch(
                        checked = listState.advancedVisible,
                        onCheckedChange = controller::onAdvancedChanged,
                    )
                }
            }
        }

        Spacer(Modifier
            .weight(1f)
            .defaultMinSize(minHeight = AckTheme.spacing.content))

        BuildInfo(
            buildInfo = viewModel.buildInfoState.collectAsState().value,
            onVersionLongPress = controller::onVersionLongPress,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = stringResource(R.string.settings_author_line),
            style = AckTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(AckTheme.spacing.content))
        TextButton(controller::onAckLicenseClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.settings_ack_license))
        }
        TextButton(controller::onLicensesClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(stringResource(R.string.settings_licenses))
        }
    }
}

@Composable
private fun LicenseRow(
    state: State<LicenseViewState>,
    controller: SettingsController,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AckTheme.spacing.gutter, vertical = AckTheme.spacing.content),
    ) {
        when (val licenseState = state.value) {
            LicenseViewState.Initial -> {}
            LicenseViewState.Unconfigured -> Button(onClick = controller::onCallsignEditClick) {
                Text("Add Callsign")
            }
            is LicenseViewState.CallsignConfigured -> CallsignChip(
                licenseState.callsign,
                licenseState is LicenseViewState.Verified,
                controller::onCallsignEditClick
            )
        }
    }
}

@Composable
private fun SettingsCard(
    group: SettingsGroup,
    controller: SettingsController,
    promptSetting: MutableState<SettingState?>,
) {
    Card(modifier = Modifier.padding(vertical = AckTheme.spacing.item)) {
        Column {
            SettingsCategoryRow(group.name)
            group.settings.forEach { item ->
                val setting = item.setting
                when {
                    item is SettingState.StringState && setting is StringBackedSetting<*> && setting.defaultData is Symbol -> {
                        val symbolSetting = setting as StringBackedSetting<Symbol>
                        SymbolStateRow(item, symbolSetting) {
                            promptSetting.value = item
                        }
                    }
                    item is SettingState.BooleanState -> BooleanStateRow(item) {
                        controller.onSwitchSettingChanged(item, it)
                    }
                    item is SettingState.IntState -> IntStateRow(item) {
                        promptSetting.value = item
                    }
                    item is SettingState.StringState -> StringStateRow(item) {
                        promptSetting.value = item
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCategoryRow(name: String) = Row(
    Modifier.padding(horizontal = AckTheme.spacing.gutter, vertical = AckTheme.spacing.item)
) {
    Text(name, style = AckTheme.typography.h2, modifier = Modifier.padding(vertical = AckTheme.spacing.item))
}
