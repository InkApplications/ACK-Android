package com.inkapplications.aprs.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ PacketEntity::class ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
internal abstract class PacketDatabase: RoomDatabase() {
    abstract fun pinsDao(): PacketDao
}
