package com.inkapplications.android.extensions.format

import com.inkapplications.android.extensions.R
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * Leverages Android's string resources to create time strings.
 */
@Reusable
class AndroidResourceDateTimeFormatter @Inject constructor(
    stringResources: StringResources,
    private val timeZone: TimeZone,
    private val clock: Clock,
): DateTimeFormatter {
    private val timeFormat = SimpleDateFormat(stringResources.getString(R.string.datetime_format_time)).also {
        it.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }
    private val dateTimeFormat = SimpleDateFormat(stringResources.getString(R.string.datetime_format_datetime)).also {
        it.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }
    private val fullTimestampFormat = SimpleDateFormat(stringResources.getString(R.string.datetime_format_timestamp)).also {
        it.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }

    override fun formatTimestamp(instant: Instant): String  {
        val now = clock.now().toLocalDateTime(timeZone)
        val local = instant.toLocalDateTime(timeZone)

        return when {
            local.year == now.year && local.dayOfYear == now.dayOfYear -> timeFormat.format(instant.toEpochMilliseconds())
            local.year == now.year && local.month == now.month -> dateTimeFormat.format(instant.toEpochMilliseconds())
            else -> fullTimestampFormat.format(instant.toEpochMilliseconds())
        }
    }
}
