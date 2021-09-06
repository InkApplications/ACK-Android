package com.inkapplications.aprs.android.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.android.extensions.compose.ui.longClickable
import com.inkapplications.aprs.android.BuildConfig
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme
import com.inkapplications.aprs.android.ui.NavigationRow

@Composable
fun SettingsScreen(
    state: State<List<SettingsGroup>>,
    onIntClicked: (SettingState.IntState) -> Unit,
    onStringClicked: (SettingState.StringState) -> Unit,
    onSwitchChanged: (SettingState.BooleanState, Boolean) -> Unit,
    onVersionLongPress: () -> Unit,
    onBackPressed: () -> Unit,
) = AprsScreen {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),

        ) {
        NavigationRow(
            title = stringResource(R.string.settings_title),
            onBackPressed = onBackPressed,
        )
        state.value.forEach { group ->
            Card(modifier = Modifier.padding(vertical = AprsTheme.Spacing.item)) {
                Column {
                    SettingsCategoryRow(group.name)
                    group.settings.forEach { item ->
                        when (item) {
                            is SettingState.BooleanState -> BooleanStateRow(item) {
                                onSwitchChanged(item, it)
                            }
                            is SettingState.IntState -> IntStateRow(item) {
                                onIntClicked(item)
                            }
                            is SettingState.StringState -> StringStateRow(item) {
                                onStringClicked(item)
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
                .longClickable(onVersionLongPress)
                .padding(AprsTheme.Spacing.clickSafety)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SettingsCategoryRow(name: String) = Row(
    Modifier.padding(horizontal = AprsTheme.Spacing.gutter, vertical = AprsTheme.Spacing.item)
) {
    Text(name, style = AprsTheme.Typography.h2)
}
