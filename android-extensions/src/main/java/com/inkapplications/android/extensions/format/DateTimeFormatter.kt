package com.inkapplications.android.extensions.format

import kotlinx.datetime.Instant

/**
 * Format date/time strings in the application
 */
interface DateTimeFormatter {
    /**
     * Format used for items with a timestamp, such as messages or logs.
     */
    fun formatTimestamp(instant: Instant): String
}
