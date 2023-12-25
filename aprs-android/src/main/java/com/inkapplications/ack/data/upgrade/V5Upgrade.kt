package com.inkapplications.ack.data.upgrade

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object V5Upgrade: Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE packets ADD COLUMN `comment` TEXT DEFAULT NULL")
    }
}
