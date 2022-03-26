package com.inkapplications.ack.data.upgrade

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.inkapplications.ack.data.PacketDatabase
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.structures.*
import com.inkapplications.ack.structures.station.StationAddress
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Version4Test {
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        PacketDatabase::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun typeMigration() {
        helper.createDatabase("v4-upgrade-test", 3).apply {
            insert("packets", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("id", 1L)
                put("timestamp", 123L)
                put("data", "Test Data")
                put("packetSource", "Ax25")
                put("sourceCallsign", "KE0YOG")
            })

            close()
        }


        val fakeParser = object: AprsCodec {
            override fun fromAx25(packet: ByteArray): AprsPacket = AprsPacket(
                route = PacketRoute(
                    source = StationAddress("KE0YOG", "72"),
                    destination = StationAddress("TEST"),
                    digipeaters = emptyList(),
                ),
                data = PacketData.StatusReport(
                    status = "",
                )
            )
            override fun fromString(packet: String): AprsPacket = TODO()
            override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray = TODO()
            override fun toString(packet: AprsPacket, config: EncodingConfig): String = TODO()
        }

        val migration = V4Upgrade(fakeParser)
        val new = helper.runMigrationsAndValidate("v4-upgrade-test", 4, true, migration)

        assertEquals(1, new.query("SELECT COUNT(*) FROM `packets`").also { it.moveToFirst() }.getInt(0))
        assertEquals("StatusReport", new.query("SELECT `dataType` FROM `packets` WHERE `id`=1").also { it.moveToFirst() }.getString(0))
        assertNull(new.query("SELECT `addresseeCallsign` FROM `packets` WHERE `id`=1").also { it.moveToFirst() }.getString(0))
    }

    @Test
    fun addresseeMigration() {
        helper.createDatabase("v4-upgrade-test", 3).apply {
            insert("packets", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("id", 1L)
                put("timestamp", 123L)
                put("data", "Test Data")
                put("packetSource", "Ax25")
                put("sourceCallsign", "KE0YOG")
            })

            close()
        }


        val fakeParser = object: AprsCodec {
            override fun fromAx25(packet: ByteArray): AprsPacket = AprsPacket(
                route = PacketRoute(
                    source = StationAddress("KE0YOG", "72"),
                    destination = StationAddress("TEST"),
                    digipeaters = emptyList(),
                ),
                data = PacketData.Message(
                    addressee = StationAddress("KE0YOF", "5"),
                    message = "Hello World"
                )
            )
            override fun fromString(packet: String): AprsPacket = TODO()
            override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray = TODO()
            override fun toString(packet: AprsPacket, config: EncodingConfig): String = TODO()
        }

        val migration = V4Upgrade(fakeParser)
        val new = helper.runMigrationsAndValidate("v4-upgrade-test", 4, true, migration)

        assertEquals(1, new.query("SELECT COUNT(*) FROM `packets`").also { it.moveToFirst() }.getInt(0))
        assertEquals("KE0YOF", new.query("SELECT `addresseeCallsign` FROM `packets` WHERE `id`=1").also { it.moveToFirst() }.getString(0))
    }

    @Test
    fun failedPacketCleanup() {
        helper.createDatabase("v4-upgrade-test", 3).apply {
            insert("packets", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("id", 1L)
                put("timestamp", 123L)
                put("data", "Test Data")
                put("packetSource", "Ax25")
                put("sourceCallsign", "KE0YOG")
            })

            close()
        }

        val fakeParser = object: AprsCodec {
            override fun fromAx25(packet: ByteArray): AprsPacket = TODO()
            override fun fromString(packet: String): AprsPacket = TODO()
            override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray = TODO()
            override fun toString(packet: AprsPacket, config: EncodingConfig): String = TODO()
        }

        val migration = V4Upgrade(fakeParser)
        val new = helper.runMigrationsAndValidate("v4-upgrade-test", 4, true, migration)

        assertEquals(0, new.query("SELECT COUNT(*) FROM `packets`").also { it.moveToFirst() }.getInt(0))
    }

    @Test
    fun typeLock() {
        // Adding new types if fine, but these type names need to be migrated if they ever change.
        assertEquals("Position", PacketData.Position::class.java.simpleName)
        assertEquals("Weather", PacketData.Weather::class.java.simpleName)
        assertEquals("ObjectReport", PacketData.ObjectReport::class.java.simpleName)
        assertEquals("ItemReport", PacketData.ItemReport::class.java.simpleName)
        assertEquals("Message", PacketData.Message::class.java.simpleName)
        assertEquals("TelemetryReport", PacketData.TelemetryReport::class.java.simpleName)
        assertEquals("StatusReport", PacketData.StatusReport::class.java.simpleName)
        assertEquals("CapabilityReport", PacketData.CapabilityReport::class.java.simpleName)
        assertEquals("Unknown", PacketData.Unknown::class.java.simpleName)
    }
}
