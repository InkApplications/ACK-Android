package com.inkapplications.ack.android.settings

/**
 * How/when to display a setting.
 */
enum class SettingVisibility {
    /**
     * Displayed to the user normally in the UI.
     */
    Visible,

    /**
     * Displayed only if the user chooses to display advanced settings.
     */
    Advanced,

    /**
     * Displayed only as a partially hidden developer setting.
     */
    Dev,
}
