package com.inkapplications.aprs.android.settings

/**
 * Contributor to the list of application-wide available settings.
 */
interface SettingsProvider {
    val settings: List<Setting>
}