package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.maps.MapViewModel

sealed interface StationViewState {
    object Initial: StationViewState
    data class Loaded(
        val insight: InsightViewState,
        val packets: List<LogItemViewState>,
        val mapState: MapViewModel,
    ): StationViewState
}
