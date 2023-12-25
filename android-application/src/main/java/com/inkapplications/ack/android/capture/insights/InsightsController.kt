package com.inkapplications.ack.android.capture.insights

import com.inkapplications.ack.android.log.LogItemViewState

/**
 * User actions available on the insights screen.
 */
interface InsightsController {
    /**
     * Invoked when the user clicks on a station in the nearby frequency list.
     */
    fun onStationItemClicked(item: LogItemViewState)
}
