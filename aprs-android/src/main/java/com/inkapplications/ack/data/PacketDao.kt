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

    @Query("SELECT * FROM packets WHERE UPPER(sourceCallsign) = UPPER(:callsign) LIMIT :limit")
    fun findBySourceCallsign(callsign: String, limit: Int): Flow<List<PacketEntity>>

    @Query("SELECT count(*) FROM packets")
    fun countAll(): Flow<Int>

    @Query("SELECT count(DISTINCT sourceCallsign) FROM packets")
    fun countSources(): Flow<Int>

    @Query("SELECT * FROM packets WHERE dataType = :type ORDER BY timestamp DESC LIMIT 1")
    fun findMostRecentByType(type: String): Flow<PacketEntity>

    @Query("""
        SELECT * FROM packets 
        WHERE (UPPER(sourceCallsign) = UPPER(:addresseeCallsign) AND UPPER(addresseeCallsign) = UPPER(:callsign))
        OR (UPPER(sourceCallsign) = UPPER(:callsign) AND UPPER(addresseeCallsign) = UPPER(:addresseeCallsign))
    """)
    fun findConversation(addresseeCallsign: String, callsign: String): Flow<List<PacketEntity>>

    @Query("""
        SELECT * FROM (
            SELECT sourceCallsign as filterkey, * FROM packets
            WHERE UPPER(addresseeCallsign) = UPPER(:callsign)
            UNION
            SELECT addresseeCallsign as filterkey, * FROM packets
            WHERE (UPPER(sourceCallsign) = UPPER(:callsign) AND addresseeCallsign IS NOT NULL)
            ORDER BY timestamp DESC
        )
        GROUP BY UPPER(filterkey)
    """)
    fun findLatestConversationMessages(callsign: String): Flow<List<PacketEntity>>

    @Insert
    suspend fun addPacket(packet: PacketEntity): Long
}
