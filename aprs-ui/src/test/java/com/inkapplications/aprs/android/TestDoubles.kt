package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.aprs.android.settings.*
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.aprs.data.CapturedPacket
import com.inkapplications.aprs.data.ConnectionConfiguration
import com.inkapplications.aprs.data.PacketSource
import com.inkapplications.karps.structures.Address
import com.inkapplications.karps.structures.AprsPacket
import com.inkapplications.karps.structures.Precipitation
import com.inkapplications.karps.structures.WindData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant

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
}

object TestPackets {
    val unknown = AprsPacket.Unknown(
        raw = "unknown packet data",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '?',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG"),
        digipeaters = listOf(),
        body = "Unknown Packet Data"
    )
    val weather = AprsPacket.Weather(
        raw = "test weather packet",
        received = Instant.DISTANT_PAST,
        dataTypeIdentifier = '!',
        source = Address("KE0YOG"),
        destination = Address("KE0YOG"),
        digipeaters = listOf(),
        timestamp = Instant.DISTANT_PAST,
        windData = WindData(null, null, null),
        precipitation = Precipitation(),
        coordinates = null,
        symbol = null,
        temperature = null,
        humidity = null,
        pressure = null,
        irradiance = null,
    )

    fun AprsPacket.toTestCapturedPacket() = CapturedPacket(
        id = 1,
        received = 0,
        data = this,
        source = PacketSource.AprsIs,
    )
}
