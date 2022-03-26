package com.inkapplications.android.extensions.format

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.StubStringResources
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidResourceDateTimeFormatterTest {
    @Test
    fun timestampFormat() {
        val stubResource = object: StringResources by StubStringResources {
            override fun getString(key: Int): String = "yyyy-MM-dd HH:mm"
        }
        val formatter = AndroidResourceDateTimeFormatter(stubResource, TimeZone.UTC)

        val result = formatter.formatTimestamp(Instant.fromEpochMilliseconds(-22073104000))

        assertEquals("1969-04-20 12:34", result)
    }
}
