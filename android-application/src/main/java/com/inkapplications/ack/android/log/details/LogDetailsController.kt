package com.inkapplications.ack.android.log.details

import com.inkapplications.ack.structures.station.Callsign

/**
 * Actions available on the log details screen
 */
interface LogDetailsController {
    /**
     * Invoked when the user taps the up navigation at the top of the screen.
     */
    fun onBackPressed()

    /**
     * Invoked when the user clicks on the station details button.
     */
    fun onViewStationDetails(station: Callsign)
}
