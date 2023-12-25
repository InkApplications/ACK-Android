package com.inkapplications.ack.data.upgrade

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.structures.AprsPacket
import kimchi.logger.KimchiLogger

/**
 * Migrates denormalized packet data by extracting the raw packet and parsing it.
 */
abstract class PacketMigration(
    targetVersion: Int,
    private val aprsCodec: AprsCodec,
    private val logger: KimchiLogger,
): Migration(targetVersion - 1, targetVersion) {
    override fun migrate(database: SupportSQLiteDatabase) {
        migrateTable(database)

        val cursor = database.query("SELECT id,data,packetSource FROM packets")
        cursor.use { cursor ->
            cursor.moveToFirst()
            do {
                val id = cursor.getLong(0)
                val data = cursor.getBlob(1)
                val source = cursor.getString(2)

                try {
                    val parsed = when (source) {
                        "Ax25" -> aprsCodec.fromAx25(data)
                        "AprsIs" -> aprsCodec.fromString(data.toString(Charsets.UTF_8))
                        else -> throw IllegalStateException("Unknown source type: $source")
                    }


                    database.update(
                        "packets",
                        SQLiteDatabase.CONFLICT_FAIL,
                        ContentValues().apply {
                            migratePacket(parsed)
                        },
                        "id = ?",
                        arrayOf(id),
                    )
                } catch (e: Throwable) {
                    logger.warn("Unable to migrate: $id")
                    database.delete("packets", "id = ?", arrayOf(id))
                }
            } while (cursor.moveToNext())
        }
    }

    protected abstract fun ContentValues.migratePacket(parsed: AprsPacket)
    protected open fun migrateTable(database: SupportSQLiteDatabase) {}
}
