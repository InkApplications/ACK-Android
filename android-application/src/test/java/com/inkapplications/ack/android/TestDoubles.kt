package com.inkapplications.ack.android

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.IntSetting
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.StringSetting
import com.inkapplications.ack.data.drivers.PacketDriver
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.station.Callsign
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Spits arguments back out as values for testing.
 */
object ParrotStringResources: StringResources {
    override fun getString(key: Int): String = ""
    override fun getString(key: Int, vararg arguments: Any): String = arguments.joinToString("|")
}

object StubSettings: SettingsReadAccess {
    override fun observeStringState(setting: StringSetting): Flow<String?> = flow {}
    override fun observeIntState(setting: IntSetting): Flow<Int?> = flow {}
    override fun observeBooleanState(setting: BooleanSetting): Flow<Boolean?> = flow {}
}

object AprsAccessStub: PacketDriver {
    override val incoming: Flow<CapturedPacket> = flow {}
    override suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig) {}
    override suspend fun connect() {}
}

object PacketStorageStub: PacketStorage {
    override fun findRecent(count: Int): Flow<List<CapturedPacket>> = flow {}
    override fun findByAddressee(callsign: Callsign): Flow<List<CapturedPacket>> = flow {}
    override fun findById(id: Long): Flow<CapturedPacket?> = flow {}
    override suspend fun save(data: ByteArray, packet: AprsPacket, source: PacketSource): CapturedPacket = TODO()
}
