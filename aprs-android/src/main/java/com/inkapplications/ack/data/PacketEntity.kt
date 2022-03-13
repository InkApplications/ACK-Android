package com.inkapplications.ack.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packets")
internal class PacketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val timestamp: Long,
    val data: ByteArray,
    @ColumnInfo(defaultValue = "Ax25")
    val packetSource: PacketSource,
    val sourceCallsign: String,
    val addresseeCallsign: String?,
    val dataType: String,
)
