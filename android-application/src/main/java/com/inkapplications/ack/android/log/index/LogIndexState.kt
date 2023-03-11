package com.inkapplications.ack.android.log.index

import com.inkapplications.ack.android.log.LogItemViewState

/**
 * Screen-state representation of the Log item screen.
 */
sealed interface LogIndexState {
    /**
     * Used when nothing has loaded yet.
     */
    object Initial: LogIndexState

    /**
     * Used when the list has been loaded, but there are no items to display.
     */
    object Empty: LogIndexState

    /**
     * Loaded a list of log items to be displayed to the user.
     */
    data class LogList(val logs: List<LogItemViewState>): LogIndexState
}
