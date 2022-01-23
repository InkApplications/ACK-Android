package com.inkapplications.aprs.android.settings

interface SettingsController {
    fun onIntSettingChanged(state: SettingState.IntState, newValue: Int)
    fun onStringSettingChanged(state: SettingState.StringState, newValue: String)
    fun onSwitchSettingChanged(state: SettingState.BooleanState, newState: Boolean)
    fun onVersionLongPress()
    fun onBackPressed()
    fun onCallsignEditClick()
}
