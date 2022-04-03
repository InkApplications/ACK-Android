package com.inkapplications.ack.android.transmit

import com.inkapplications.ack.structures.Digipeater
import com.inkapplications.ack.structures.station.StationAddress
import kotlin.test.Test
import kotlin.test.assertEquals

class PathTransformerTest {
    @Test
    fun toStorage() {
        val input = listOf(
            Digipeater(StationAddress("KE0YOG")),
        )

        val result = PathTransformer.toStorage(input)

        assertEquals("KE0YOG", result)
    }

    @Test
    fun toStorageMultiple() {
        val input = listOf(
            Digipeater(StationAddress("KE0YOG")),
            Digipeater(StationAddress("KE0YOF", "2")),
        )

        val result = PathTransformer.toStorage(input)

        assertEquals("KE0YOG,KE0YOF-2", result)
    }

    @Test
    fun toData() {
        val expected = listOf(
            Digipeater(StationAddress("KE0YOG")),
        )
        val result = PathTransformer.toData("KE0YOG")

        assertEquals(expected, result)
    }

    @Test
    fun toDataMultiple() {
        val expected = listOf(
            Digipeater(StationAddress("KE0YOG")),
            Digipeater(StationAddress("KE0YOF", "2")),
        )
        val result = PathTransformer.toData("KE0YOG,KE0YOF-2")

        assertEquals(expected, result)
    }
}
