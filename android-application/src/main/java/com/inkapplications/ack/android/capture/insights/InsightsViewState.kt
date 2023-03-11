package com.inkapplications.ack.android.capture.insights

sealed interface InsightsViewState {
    object Initial: InsightsViewState
    object Empty: InsightsViewState
    data class Loaded(
        val temperature: String,
        val weatherReporter: String,
        val weatherReportTime: String,
        val weatherVisible: Boolean,
        val packets: Int,
        val stations: Int,
    ): InsightsViewState
}
