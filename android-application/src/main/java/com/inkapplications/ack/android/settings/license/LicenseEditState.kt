package com.inkapplications.ack.android.settings.license

/**
 * State of the license edit screen.
 */
sealed interface LicenseEditState {
    /**
     * Initial state of the screen before data has been loaded.
     */
    object Initial: LicenseEditState

    /**
     * State of the screen when the user's existing license is loaded.
     */
    data class Editable(
        val initialValues: LicensePromptFieldValues,
    ): LicenseEditState
}
