package com.inkapplications.aprs.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ PacketEntity::class ],
    version = 1
)
internal abstract class PacketDatabase: RoomDatabase() {
    abstract fun pinsDao(): PacketDao
}
