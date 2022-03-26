package com.inkapplications.android.extensions.format

import com.inkapplications.android.extensions.R
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 * Leverages Android's string resources to create time strings.
 */
@Reusable
class AndroidResourceDateTimeFormatter @Inject constructor(
    stringResources: StringResources,
    timeZone: TimeZone,
): DateTimeFormatter {
    private val timestampFormat = SimpleDateFormat(stringResources.getString(R.string.datetime_format_timestamp)).also {
        it.timeZone = java.util.TimeZone.getTimeZone(timeZone.id)
    }

    override fun formatTimestamp(instant: Instant): String = instant.toEpochMilliseconds().let(timestampFormat::format)
}
