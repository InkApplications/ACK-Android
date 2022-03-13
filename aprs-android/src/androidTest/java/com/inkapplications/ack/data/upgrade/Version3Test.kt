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
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Version3Test {
    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        PacketDatabase::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun sourceExtraction() {
        helper.createDatabase("v3-upgrade-test", 2).apply {
            insert("packets", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("id", 1L)
                put("timestamp", 123L)
                put("data", "Test Data")
                put("packetSource", "Ax25")
            })

            close()
        }

        val fakeParser = object: AprsCodec {
            override fun fromAx25(packet: ByteArray): AprsPacket = AprsPacket(
                route = PacketRoute(
                    source = Address("KE0YOG", "72"),
                    destination = Address("TEST"),
                    digipeaters = emptyList(),
                ),
                data = PacketData.Unknown(
                    body = "",
                )
            )
            override fun fromString(packet: String): AprsPacket = TODO()
            override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray = TODO()
            override fun toString(packet: AprsPacket, config: EncodingConfig): String = TODO()
        }

        val migration = V3Upgrade(fakeParser)
        val new = helper.runMigrationsAndValidate("v3-upgrade-test", 3, true, migration)

        assertEquals(1, new.query("SELECT COUNT(*) FROM `packets`").also { it.moveToFirst() }.getInt(0))
        assertEquals("KE0YOG", new.query("SELECT `sourceCallsign` FROM `packets` WHERE `id`=1").also { it.moveToFirst() }.getString(0))
    }

    @Test
    fun isExtraction() {
        helper.createDatabase("v3-upgrade-test", 2).apply {
            insert("packets", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("id", 1L)
                put("timestamp", 123L)
                put("data", "Test Data")
                put("packetSource", "AprsIs")
            })

            close()
        }


        val fakeParser = object: AprsCodec {
            override fun fromAx25(packet: ByteArray): AprsPacket = TODO()
            override fun fromString(packet: String): AprsPacket = AprsPacket(
                route = PacketRoute(
                    source = Address("KE0YOG", "72"),
                    destination = Address("TEST"),
                    digipeaters = emptyList(),
                ),
                data = PacketData.Unknown(
                    body = "",
                )
            )
            override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray = TODO()
            override fun toString(packet: AprsPacket, config: EncodingConfig): String = TODO()
        }

        val migration = V3Upgrade(fakeParser)
        val new = helper.runMigrationsAndValidate("v3-upgrade-test", 3, true, migration)

        assertEquals(1, new.query("SELECT COUNT(*) FROM `packets`").also { it.moveToFirst() }.getInt(0))
        assertEquals("KE0YOG", new.query("SELECT `sourceCallsign` FROM `packets` WHERE `id`=1").also { it.moveToFirst() }.getString(0))
    }

    @Test
    fun failedPacketCleanup() {
        helper.createDatabase("v3-upgrade-test", 2).apply {
            insert("packets", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
                put("id", 1L)
                put("timestamp", 123L)
                put("data", "Test Data")
                put("packetSource", "Ax25")
            })

            close()
        }

        val fakeParser = object: AprsCodec {
            override fun fromAx25(packet: ByteArray): AprsPacket = TODO()
            override fun fromString(packet: String): AprsPacket = TODO()
            override fun toAx25(packet: AprsPacket, config: EncodingConfig): ByteArray = TODO()
            override fun toString(packet: AprsPacket, config: EncodingConfig): String = TODO()
        }

        val migration = V3Upgrade(fakeParser)
        val new = helper.runMigrationsAndValidate("v3-upgrade-test", 3, true, migration)

        assertEquals(0, new.query("SELECT COUNT(*) FROM `packets`").also { it.moveToFirst() }.getInt(0))
    }
}
