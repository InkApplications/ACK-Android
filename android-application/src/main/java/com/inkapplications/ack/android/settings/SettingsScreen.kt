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
import com.inkapplications.android.extensions.compose.ui.longClickable
import com.inkapplications.ack.android.BuildConfig
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.IntPrompt
import com.inkapplications.ack.android.input.StringPrompt
import com.inkapplications.ack.android.ui.CallsignChip
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.android.ui.NavigationRow

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
        when (val settingState = promptSetting.value) {
            is SettingState.IntState -> IntPrompt(
                title = settingState.setting.name,
                value = settingState.value,
                validator = settingState.setting.validator,
                onDismiss = { promptSetting.value = null },
                onSubmit = {
                    controller.onIntSettingChanged(settingState, it)
                    promptSetting.value = null
                }
            )
            is SettingState.StringState -> StringPrompt(
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

        when (val settingsState = viewModel.settingsList.collectAsState().value) {
            SettingsListViewState.Initial -> {}
            is SettingsListViewState.Loaded -> settingsState.settingsList.forEach {
                SettingsCard(group = it, controller = controller, promptSetting = promptSetting)
            }
        }

        Spacer(Modifier
            .weight(1f)
            .defaultMinSize(minHeight = AckTheme.spacing.content))
        Text(
            text = stringResource(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString()),
            style = AckTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .longClickable(controller::onVersionLongPress)
                .padding(AckTheme.spacing.clickSafety)
                .align(Alignment.CenterHorizontally)
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
                when (item) {
                    is SettingState.BooleanState -> BooleanStateRow(item) {
                        controller.onSwitchSettingChanged(item, it)
                    }
                    is SettingState.IntState -> IntStateRow(item) {
                        promptSetting.value = item
                    }
                    is SettingState.StringState -> StringStateRow(item) {
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
