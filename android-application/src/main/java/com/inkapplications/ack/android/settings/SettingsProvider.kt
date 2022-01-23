package com.inkapplications.ack.android.settings

/**
 * Contributor to the list of application-wide available settings.
 */
interface SettingsProvider {
    val settings: List<Setting>
}
