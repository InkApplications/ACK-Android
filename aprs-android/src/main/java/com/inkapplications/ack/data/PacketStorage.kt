package com.inkapplications.ack.data

import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.station.Callsign
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * Provides query access to packets that have been captured and stored.
 */
interface PacketStorage {
    /**
     * Find a limited number of packets by recency.
     *
     * @param count The max limit of packets to find.
     */
    fun findRecent(count: Int): Flow<List<CapturedPacket>>

    /**
     * Find the latest packet in every conversation.
     *
     * @param callsign The callsign to find conversations with.
     */
    fun findLatestByConversation(callsign: Callsign): Flow<List<CapturedPacket>>

    /**
     * Find all packets in a specific conversation.
     *
     * @param addressee The external callsign to look up conversations with.
     * @param callsign The user's callsign to look up conversations with.
     */
    fun findConversation(addressee: Callsign, callsign: Callsign): Flow<List<CapturedPacket>>

    /**
     * Find a specific packet by its id
     *
     * @param id The locally generated ID to find a packet by
     */
    fun findById(id: Long): Flow<CapturedPacket?>

    /**
     * Find the quantity of packets saved.
     */
    fun count(): Flow<Int>

    /**
     * Find the number of distinct stations in the packets received.
     */
    fun countStations(): Flow<Int>

    /**
     * Find packets with comments that appear to be broadcasting a station frequency.
     */
    fun findByStationComments(limit: Int? = null): Flow<List<CapturedPacket>>

    /**
     * Find the most recent packet received of a specific data type.
     *
     * @param type The data type of the packet to find.
     */
    fun findMostRecentByType(type: KClass<out PacketData>): Flow<CapturedPacket?>

    /**
     * Find all of the packets received from a callsign.
     *
     * @param callsign The source/origin callsign to find packets of.
     */
    fun findBySource(callsign: Callsign, limit: Int? = null): Flow<List<CapturedPacket>>

    /**
     * Store a packet locally.
     *
     * @param data The raw data of the packet received.
     * @param packet The parsed version of the packet data,
     * @param source The origin of the packet data.
     */
    suspend fun save(data: ByteArray, packet: AprsPacket, source: PacketSource): CapturedPacket
}
