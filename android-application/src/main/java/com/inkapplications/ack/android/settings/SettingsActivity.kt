package com.inkapplications.ack.android.settings

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.inkapplications.ack.android.settings.agreement.UserAgreementActivity
import com.inkapplications.ack.android.settings.license.LicenseEditActivity
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.android.startActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.intProperty
import kimchi.analytics.stringProperty
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity: ExtendedActivity(), SettingsController {
    @Inject
    lateinit var settingsAccess: SettingsAccess

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate() {
        super.onCreate()

        setContent {
            SettingsScreen(
                viewModel = viewModel,
                controller = this,
            )
        }
    }

    override fun onVersionLongPress() {
        Kimchi.trackEvent("settings_show_advanced")
        viewModel.showDev()
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

    override fun onAckLicenseClick() {
        startActivity(UserAgreementActivity::class)
    }

    override fun onLicensesClick() {
        Kimchi.trackEvent("settings_show_licenses")
        startActivity(OssLicensesMenuActivity::class)
    }

    override fun onAdvancedChanged(checked: Boolean) {
        viewModel.showAdvanced(checked)
    }
}
