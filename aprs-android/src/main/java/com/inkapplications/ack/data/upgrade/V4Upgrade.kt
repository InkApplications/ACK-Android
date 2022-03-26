package com.inkapplications.ack.data.upgrade

import android.content.ContentValues
import androidx.sqlite.db.SupportSQLiteDatabase
import com.inkapplications.ack.codec.AprsCodec
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import kimchi.logger.EmptyLogger
import kimchi.logger.KimchiLogger

internal class V4Upgrade(
    aprsCodec: AprsCodec,
    logger: KimchiLogger = EmptyLogger,
): PacketMigration(4, aprsCodec, logger) {
    override fun migrateTable(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE packets ADD COLUMN `addresseeCallsign` TEXT DEFAULT NULL")
        database.execSQL("ALTER TABLE packets ADD COLUMN `dataType` TEXT NOT NULL DEFAULT 'Unknown'")
    }

    override fun ContentValues.migratePacket(parsed: AprsPacket) {
        put("addresseeCallsign", (parsed.data as? PacketData.Message)?.addressee?.callsign?.canonical)
        put("dataType", parsed.data.javaClass.simpleName)
    }
}
