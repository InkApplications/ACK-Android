package com.inkapplications.aprs.android.settings

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.*
import com.inkapplications.aprs.android.prompt.intPrompt
import com.inkapplications.aprs.android.prompt.stringPrompt
import com.inkapplications.aprs.android.settings.license.LicenseEditActivity
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kimchi.analytics.stringProperty

class SettingsActivity: ExtendedActivity(), SettingsController {
    private lateinit var settingsAccess: SettingsAccess

    override fun onCreate() {
        super.onCreate()
        settingsAccess = component.settingsAccess()

        setContent {
            val state = settingsAccess.settingsViewModel.collectAsState(null)

            SettingsScreen(
                state = state,
                controller = this,
            )
        }
    }

    override fun onVersionLongPress() {
        Kimchi.trackEvent("settings_show_advanced")
        settingsAccess.showAdvancedSettings()
    }

    override fun onCallsignEditClick() {
        Kimchi.trackEvent("settings_license_edit")
        startActivity(LicenseEditActivity::class)
    }

    override fun onIntSettingClicked(state: SettingState.IntState) {
        intPrompt(state.name, state.value) { result ->
            Kimchi.trackEvent("settings_change", listOf(
                stringProperty("setting", state.key),
                intProperty("value", result)
            ))
            settingsAccess.updateInt(state.key, result)
        }
    }

    override fun onStringSettingClicked(state: SettingState.StringState) {
        stringPrompt(state.name, state.value) { result ->
            Kimchi.trackEvent("settings_change", listOf(
                stringProperty("setting", state.key),
                stringProperty("value", result)
            ))
            settingsAccess.updateString(state.key, result)
        }
    }

    override fun onSwitchSettingChanged(state: SettingState.BooleanState, newState: Boolean) {
        Kimchi.trackEvent("settings_change", listOf(
            stringProperty("setting", state.key),
            stringProperty("value", newState.toString())
        ))
        settingsAccess.updateBoolean(state.key, newState)
    }
}
