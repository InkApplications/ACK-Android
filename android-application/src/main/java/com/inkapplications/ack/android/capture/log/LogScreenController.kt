package com.inkapplications.ack.android.capture.log

/**
 * Actions invoked from the Log Index screen
 */
interface LogScreenController {
    /**
     * Invoked when the user clicks a log item in the log list.
     */
    fun onLogListItemClick(item: LogItemViewModel)
}
