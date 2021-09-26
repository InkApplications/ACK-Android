package com.inkapplications.aprs.android.settings

interface SettingsController {
    fun onIntSettingClicked(state: SettingState.IntState)
    fun onStringSettingClicked(state: SettingState.StringState)
    fun onSwitchSettingChanged(state: SettingState.BooleanState, newState: Boolean)
    fun onVersionLongPress()
    fun onBackPressed()
    fun onCallsignEditClick()
}
