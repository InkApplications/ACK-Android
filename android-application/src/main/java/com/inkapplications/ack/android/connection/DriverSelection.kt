package com.inkapplications.ack.android.connection

import androidx.annotation.StringRes
import com.inkapplications.ack.android.R

/**
 * Represents the user-selected source of APRS data.
 */
enum class DriverSelection {
    Audio,
    Internet,
    Tnc,
}

/**
 * Resource ID of the user-readable name for the selected driver.
 */
val DriverSelection.readableName get(): @StringRes Int = when (this) {
    DriverSelection.Audio -> R.string.capture_driver_selection_audio
    DriverSelection.Internet -> R.string.capture_driver_selection_internet
    DriverSelection.Tnc -> R.string.capture_driver_selection_tnc
}
