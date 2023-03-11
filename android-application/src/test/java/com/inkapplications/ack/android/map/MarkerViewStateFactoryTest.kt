package com.inkapplications.ack.android.map

import android.graphics.Bitmap
import com.inkapplications.ack.android.symbol.SymbolFactory
import com.inkapplications.ack.android.symbol.SymbolFactoryDummy
import com.inkapplications.ack.android.toTestCapturedPacket
import com.inkapplications.ack.android.toTestPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.Symbol
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MarkerViewStateFactoryTest {
    val symbolFactoryStub = object: SymbolFactory {
        override val defaultSymbol: Bitmap? get() = TODO()
        override fun createSymbol(symbol: Symbol): Bitmap? = TODO()
    }

    @Test
    fun unknown() {
        val factory = MarkerViewStateFactory(symbolFactoryStub)
        val packet = PacketData.Unknown(body = "")
        val result = factory.create(packet.toTestPacket().toTestCapturedPacket())

        assertNull(result, "Unknown packet should not produce a marker")
    }

    @Test
    fun noCoordinates() {
        val factory = MarkerViewStateFactory(symbolFactoryStub)
        val packet = PacketData.Weather()
        val result = factory.create(packet.toTestPacket().toTestCapturedPacket())

        assertNull(result, "Mappable Packet without location should not produce a marker")
    }

    @Test
    fun noSymbol() {
        val factory = MarkerViewStateFactory(SymbolFactoryDummy)
        val packet = PacketData.Weather()
        val result = factory.create(packet.toTestPacket().toTestCapturedPacket())

        assertNull(result, "Mappable Packet without location should not produce a marker")
    }

    @Test
    fun marker() {
        val symbolFactorySpy = object: SymbolFactory by symbolFactoryStub {
            var called = false
            override fun createSymbol(symbol: Symbol): Bitmap? = null.also {
                called = true
            }

        }
        val factory = MarkerViewStateFactory(symbolFactorySpy)
        val packet = PacketData.Weather(coordinates = GeoCoordinates(0.latitude, 0.longitude))
        val result = factory.create(packet.toTestPacket().toTestCapturedPacket())

        assertTrue(symbolFactorySpy.called, "Symbol is generated when coordinates specified")
    }
}
