package com.inkapplications.ack.android.settings

data class SettingsViewState(
    val settingsList: List<SettingsGroup> = emptyList(),
    val callsignText: String? = null,
    val verified: Boolean = false,
)
