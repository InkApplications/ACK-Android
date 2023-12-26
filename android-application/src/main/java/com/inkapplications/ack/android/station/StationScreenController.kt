package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.data.CaptureId

/**
 * Actions invoked on the station screen
 */
interface StationScreenController {
    /**
     * Invoked when the user presses the back button at the top of the page.
     */
    fun onBackPressed()

    /**
     * Invoked when the user clicks on a log item in the list of packets
     * received for this station.
     */
    fun onLogItemClicked(item: LogItemViewState)

    /**
     * Invoked when the user clicks on a map marker.
     *
     * @param id The id of the map marker that was clicked.
     */
    fun onMapItemClicked(id: CaptureId?)
}
