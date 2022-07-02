package com.inkapplications.ack.android.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.inkapplications.android.extensions.compose.ui.longClickable
import com.inkapplications.ack.android.BuildConfig
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.IntPrompt
import com.inkapplications.ack.android.input.StringPrompt
import com.inkapplications.ack.android.ui.CallsignChip
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.android.ui.NavigationRow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsScreen(
    state: State<SettingsViewModel?>,
    controller: SettingsController,
) = AckScreen {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        NavigationRow(
            title = stringResource(R.string.settings_title),
            onBackPressed = controller::onBackPressed,
        )
        val viewModel = state.value
        if (viewModel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AckTheme.dimensions.gutter, vertical = AckTheme.dimensions.content),
            ) {
                val callsign = viewModel.callsignText
                if (callsign != null) {
                    CallsignChip(callsign, viewModel.verified)
                } else {
                    Button(onClick = controller::onCallsignEditClick) {
                        Text("Add Callsign")
                    }
                }
            }
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
            viewModel.settingsList.forEach { group ->
                Card(modifier = Modifier.padding(vertical = AckTheme.dimensions.item)) {
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
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.application_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE.toString()),
                modifier = Modifier
                    .longClickable(controller::onVersionLongPress)
                    .padding(AckTheme.dimensions.clickSafety)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}


@Composable
fun SettingsCategoryRow(name: String) = Row(
    Modifier.padding(horizontal = AckTheme.dimensions.gutter, vertical = AckTheme.dimensions.item)
) {
    Text(name, style = AckTheme.typography.h2, modifier = Modifier.padding(vertical = AckTheme.dimensions.item))
}
