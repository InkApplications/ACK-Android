package com.inkapplications.ack.android.capture.insights

import com.inkapplications.ack.android.log.LogItemViewState

sealed interface NearbyStationsState {
    object Initial: NearbyStationsState
    object Empty: NearbyStationsState
    data class StationList(
        val stations: List<LogItemViewState>,
    ): NearbyStationsState
}
