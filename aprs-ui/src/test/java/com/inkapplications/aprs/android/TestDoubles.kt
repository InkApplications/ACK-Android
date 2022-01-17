package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.settings.BooleanSetting
import com.inkapplications.aprs.android.settings.IntSetting
import com.inkapplications.aprs.android.settings.SettingsReadAccess
import com.inkapplications.aprs.android.settings.StringSetting
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.aprs.data.ConnectionConfiguration
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.EncodingConfig
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

object AprsAccessStub: AprsAccess {
    override val incoming: Flow<CapturedPacket> = flow {}
    override fun listenForAudioPackets(): Flow<CapturedPacket> = flow {}
    override fun listenForInternetPackets(settings: ConnectionConfiguration): Flow<CapturedPacket> = flow {}
    override fun findRecent(count: Int): Flow<List<CapturedPacket>> = flow {}
    override fun findById(id: Long): Flow<CapturedPacket?> = flow {}
    override fun transmitAudioPacket(packet: AprsPacket, config: EncodingConfig) {}
}
