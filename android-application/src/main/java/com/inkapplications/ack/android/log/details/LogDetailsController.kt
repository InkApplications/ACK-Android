package com.inkapplications.ack.android.log.details

import com.inkapplications.ack.data.CaptureId
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

    /**
     * Invoked when the user clicks on a map marker.
     *
     * @param id The id of the map marker that was clicked.
     */
    fun onMapItemClicked(id: CaptureId?)
}
