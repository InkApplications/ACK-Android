package com.inkapplications.aprs.android.settings

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.extensions.startActivity
import com.inkapplications.aprs.android.*
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

    override fun onIntSettingChanged(state: SettingState.IntState, newValue: Int) {
        Kimchi.trackEvent("settings_change", listOf(
            stringProperty("setting", state.setting.key),
            intProperty("value", newValue)
        ))
        settingsAccess.updateInt(state.setting.key, newValue)
    }

    override fun onStringSettingChanged(state: SettingState.StringState, newValue: String) {
        Kimchi.trackEvent("settings_change", listOf(
            stringProperty("setting", state.setting.key),
            stringProperty("value", newValue)
        ))
        settingsAccess.updateString(state.setting.key, newValue)
    }

    override fun onSwitchSettingChanged(state: SettingState.BooleanState, newState: Boolean) {
        Kimchi.trackEvent("settings_change", listOf(
            stringProperty("setting", state.setting.key),
            stringProperty("value", newState.toString())
        ))
        settingsAccess.updateBoolean(state.setting.key, newState)
    }
}
