package com.inkapplications.ack.android.settings

/**
 * Models the state the user's license information displayed in settings.
 */
sealed interface LicenseViewState {
    /**
     * The initial state before data is loaded.
     */
    object Initial: LicenseViewState

    /**
     * Indicates that the user has not set a callsign or passcode.
     */
    object Unconfigured: LicenseViewState

    /**
     * Indicates that the user has a callsign for their license configured.
     */
    sealed interface CallsignConfigured: LicenseViewState {
        /**
         * Readable Callsign to display on the screen.
         */
        val callsign: String
    }

    /**
     * Indicates that the user has configured both a callsign, but not a passcode.
     */
    data class Registered(override val callsign: String): CallsignConfigured

    /**
     * Indicates that the user has configured both a callsign and passcode.
     */
    data class Verified(override val callsign: String): CallsignConfigured
}
