package com.inkapplications.ack.android.capture.insights

sealed interface InsightsViewState {
    object Initial: InsightsViewState
    data class InsightsViewModel(
        val temperature: String,
        val weatherReporter: String,
        val weatherReportTime: String,
        val weatherVisible: Boolean,
        val packets: Int,
        val stations: Int,
    ): InsightsViewState
}