package com.inkapplications.ack.android.settings

/**
 * Models the state of the list of settings within the settings screen.
 */
sealed interface SettingsListViewState {
    /**
     * The initial state of the settings screen before data is loaded.
     */
    object Initial: SettingsListViewState

    /**
     * The state of the settings screen after data is loaded.
     */
    data class Loaded(
        val settingsList: List<SettingsGroup>,
        val advancedVisible: Boolean,
    ): SettingsListViewState
}
