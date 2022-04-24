package com.inkapplications.android.extensions.format

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.StubStringResources
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class AndroidResourceDateTimeFormatterTest {
    @Test
    fun timestampFormatRecent() {
        val stubResource = object: StringResources by StubStringResources {
            override fun getString(key: Int): String = "HH:mm"
        }
        val time = Instant.fromEpochMilliseconds(-22073104000)
        val formatter = AndroidResourceDateTimeFormatter(stubResource, TimeZone.UTC, (time + 25.minutes).toFixedClock())

        val result = formatter.formatTimestamp(time)

        assertEquals("12:34", result)
    }

    @Test
    fun timestampFormatWithinYear() {
        val stubResource = object: StringResources by StubStringResources {
            override fun getString(key: Int): String = "MMM d, HH:mm"
        }
        val time = Instant.fromEpochMilliseconds(-22073104000)
        val formatter = AndroidResourceDateTimeFormatter(stubResource, TimeZone.UTC, (time + 25.days).toFixedClock())

        val result = formatter.formatTimestamp(time)

        assertEquals("Apr 20, 12:34", result)
    }

    @Test
    fun timestampFormatOld() {
        val stubResource = object: StringResources by StubStringResources {
            override fun getString(key: Int): String = "yyyy-MM-dd HH:mm"
        }
        val time = Instant.fromEpochMilliseconds(-22073104000)
        val formatter = AndroidResourceDateTimeFormatter(stubResource, TimeZone.UTC, (time + 500.days).toFixedClock())

        val result = formatter.formatTimestamp(time)

        assertEquals("1969-04-20 12:34", result)
    }

    private fun Instant.toFixedClock() = object: Clock {
        override fun now(): Instant = this@toFixedClock
    }
}
