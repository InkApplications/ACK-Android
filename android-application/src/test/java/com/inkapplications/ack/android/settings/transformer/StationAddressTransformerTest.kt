package com.inkapplications.ack.android.settings.transformer

import com.inkapplications.ack.structures.station.StationAddress
import kotlin.test.Test
import kotlin.test.assertEquals

class StationAddressTransformerTest {
    @Test
    fun toData() {
        assertEquals(StationAddress("KE0YOG"), StationAddressTransformer.toData("KE0YOG"))
        assertEquals(StationAddress("KE0YOG", "2"), StationAddressTransformer.toData("KE0YOG-2"))
    }

    @Test
    fun toStorage() {
        assertEquals("KE0YOG", StationAddress("KE0YOG").let(StationAddressTransformer::toStorage))
        assertEquals("KE0YOG-2", StationAddress("KE0YOG", "2").let(StationAddressTransformer::toStorage))
    }
}
