package com.inkapplications.ack.android.station

import com.inkapplications.ack.android.log.LogItemViewModel

sealed interface StationViewState {
    object Initial: StationViewState
    data class Loaded(
        val insight: InsightViewModel,
        val packets: List<LogItemViewModel>,
    ): StationViewState
}
