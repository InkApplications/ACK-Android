package com.inkapplications.aprs.data.upgrade

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inkapplications.karps.parser.AprsParser
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger
import java.lang.IllegalStateException

internal class V3Upgrade(
    private val aprsParser: AprsParser,
    private val logger: KimchiLogger = EmptyLogger,
): Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE packets ADD COLUMN `sourceCallsign` TEXT NOT NULL DEFAULT ''")

        val cursor = database.query("SELECT id,data,packetSource FROM packets")

        cursor.use { cursor ->
            cursor.moveToFirst()
            do {
                val id = cursor.getLong(0)
                val data = cursor.getBlob(1)
                val source = cursor.getString(2)

                try {
                    val parsed = when (source) {
                        "Ax25" -> aprsParser.fromAx25(data)
                        "AprsIs" -> aprsParser.fromString(data.toString(Charsets.UTF_8))
                        else -> throw IllegalStateException("Unknown source type: $source")
                    }

                    database.update(
                        "packets",
                        SQLiteDatabase.CONFLICT_FAIL,
                        ContentValues().apply {
                            put("sourceCallsign", parsed.source.callsign)
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
}
