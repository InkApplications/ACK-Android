package com.inkapplications.ack.android.log.index

import com.inkapplications.ack.android.log.LogItemViewModel

/**
 * Actions invoked from the Log Index screen
 */
interface LogIndexController {
    /**
     * Invoked when the user clicks a log item in the log list.
     */
    fun onLogListItemClick(item: LogItemViewModel)
}
