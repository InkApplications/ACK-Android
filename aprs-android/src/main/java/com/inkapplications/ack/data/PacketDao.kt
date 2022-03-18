package com.inkapplications.ack.data

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

    @Query("SELECT * FROM packets WHERE addresseeCallsign = :addresseeCallsign")
    fun findByAddresseeCallsign(addresseeCallsign: String): Flow<List<PacketEntity>>

    @Insert
    suspend fun addPacket(packet: PacketEntity): Long
}
