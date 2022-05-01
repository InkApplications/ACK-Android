package com.inkapplications.ack.android.capture.log

/**
 * Screen-state representation of the Log item screen.
 */
sealed interface LogScreenState {
    /**
     * Used when nothing has loaded yet.
     */
    object Initial: LogScreenState

    /**
     * Used when the list has been loaded, but there are no items to display.
     */
    object Empty: LogScreenState

    /**
     * Loaded a list of log items to be displayed to the user.
     */
    data class LogList(val logs: List<LogItemViewModel>): LogScreenState
}
