package com.inkapplications.ack.android.settings

import com.inkapplications.ack.structures.station.StationAddress

/**
 * Station Address + Passcode data pair.
 */
data class LicenseData(
    val address: StationAddress? = null,
    val passcode: Passcode? = null,
)
