package com.inkapplications.aprs.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packets")
internal class PacketEntity(
    val timestamp: Long,
    val data: ByteArray,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)
