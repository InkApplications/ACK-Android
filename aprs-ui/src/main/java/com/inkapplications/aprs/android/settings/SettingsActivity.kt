package com.inkapplications.aprs.android.settings

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.aprs.android.*
import com.inkapplications.aprs.android.prompt.intPrompt
import com.inkapplications.aprs.android.prompt.stringPrompt
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kimchi.analytics.stringProperty

class SettingsActivity: ExtendedActivity() {
    private lateinit var settingsAccess: SettingsAccess

    override fun onCreate() {
        super.onCreate()
        settingsAccess = component.settingsRepository()

        setContent {
            val state = settingsAccess.settingsStateGrouped.collectAsState(emptyList())

            SettingsScreen(
                state = state,
                onIntClicked = ::onIntClicked,
                onStringClicked = ::onStringClicked,
                onSwitchChanged = ::onSwitchChanged,
                onVersionLongPress = ::onVersionLongPress,
                onBackPressed = ::onBackPressed,
            )
        }
    }

    private fun onVersionLongPress() {
        Kimchi.trackEvent("settings_show_advanced")
        settingsAccess.showAdvancedSettings()
    }

    private fun onIntClicked(state: SettingState.IntState) {
        intPrompt(state.name, state.value) { result ->
            Kimchi.trackEvent("settings_change", listOf(
                stringProperty("setting", state.key),
                intProperty("value", result)
            ))
            settingsAccess.updateInt(state.key, result)
        }
    }

    private fun onStringClicked(state: SettingState.StringState) {
        stringPrompt(state.name, state.value) { result ->
            Kimchi.trackEvent("settings_change", listOf(
                stringProperty("setting", state.key),
                stringProperty("value", result)
            ))
            settingsAccess.updateString(state.key, result)
        }
    }

    private fun onSwitchChanged(state: SettingState.BooleanState, checked: Boolean) {
        Kimchi.trackEvent("settings_change", listOf(
            stringProperty("setting", state.key),
            stringProperty("value", checked.toString())
        ))
        settingsAccess.updateBoolean(state.key, checked)
    }
}
