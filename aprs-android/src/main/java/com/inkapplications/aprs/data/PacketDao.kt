package com.inkapplications.aprs.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PacketDao {
    @Query("SELECT * FROM packets ORDER BY timestamp DESC LIMIT :count")
    fun findRecent(count: Int): Flow<List<PacketEntity>>

    @Query("SELECT * FROM packets WHERE id = :id")
    fun findById(id: Long): Flow<PacketEntity?>

    @Insert
    suspend fun addPacket(packet: PacketEntity): Long
}
