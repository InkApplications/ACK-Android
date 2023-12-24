package com.inkapplications.ack.android

import com.inkapplications.ack.android.map.MarkerViewState
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.android.settings.BooleanSetting
import com.inkapplications.ack.android.settings.IntSetting
import com.inkapplications.ack.android.settings.SettingsReadAccess
import com.inkapplications.ack.android.settings.StringSetting
import com.inkapplications.ack.data.drivers.PacketDriver
import com.inkapplications.ack.data.CapturedPacket
import com.inkapplications.ack.data.PacketSource
import com.inkapplications.ack.data.PacketStorage
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.ack.structures.AprsPacket
import com.inkapplications.ack.structures.EncodingConfig
import com.inkapplications.ack.structures.PacketData
import com.inkapplications.ack.structures.PacketRoute
import com.inkapplications.ack.structures.capabilities.Mapable
import com.inkapplications.ack.structures.station.Callsign
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.android.extensions.ViewStateFactory
import com.inkapplications.android.extensions.format.DateTimeFormatter
import inkapplications.spondee.spatial.GeoCoordinates
import inkapplications.spondee.spatial.latitude
import inkapplications.spondee.spatial.longitude
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlin.reflect.KClass
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
    override val connectionState: Flow<DriverConnectionState> = flow {}
    override val incoming: Flow<CapturedPacket> = flow {}
    override suspend fun transmitPacket(packet: AprsPacket, encodingConfig: EncodingConfig) {}
    override suspend fun connect() {}
    override suspend fun disconnect() {}
}

object PacketStorageStub: PacketStorage {
    override fun findRecent(count: Int): Flow<List<CapturedPacket>> = flow {}
    override fun findLatestByConversation(callsign: Callsign): Flow<List<CapturedPacket>> = flow {}
    override fun findConversation(addressee: Callsign, callsign: Callsign): Flow<List<CapturedPacket>> = flow {}
    override fun findById(id: Long): Flow<CapturedPacket?> = flow {}
    override suspend fun save(data: ByteArray, packet: AprsPacket, source: PacketSource): CapturedPacket = TODO()
    override fun count(): Flow<Int> = flow {}
    override fun countStations(): Flow<Int> = flow {}
    override fun findMostRecentByType(type: KClass<out PacketData>): Flow<CapturedPacket?> = flow {}
    override fun findBySource(callsign: Callsign, limit: Int?): Flow<List<CapturedPacket>> = flow {}
}

object EpochFormatterFake: DateTimeFormatter {
    override fun formatTimestamp(instant: Instant): String = instant.toEpochMilliseconds().toString()
}

object NullMarkerFactoryMock: ViewStateFactory<CapturedPacket, MarkerViewState?> {
    override fun create(data: CapturedPacket): MarkerViewState? = null.also {
        assertNull((data.parsed.data as? Mapable)?.coordinates)
    }
}

val DummyMarker = MarkerViewState(0, GeoCoordinates(0.latitude, 0.longitude), null)
val DummyPacket = CapturedPacket(
    id = 0L,
    received = Instant.DISTANT_PAST,
    parsed = AprsPacket(
        route = PacketRoute(
            source = StationAddress(
                callsign = Callsign(""),
            ),
            destination = StationAddress(
                callsign = Callsign(""),
            ),
            digipeaters = emptyList(),
        ),
        data = PacketData.Unknown(""),
    ),
    source = PacketSource.Local,
    raw = byteArrayOf(),
)

object DummyMarkerFactoryMock: ViewStateFactory<CapturedPacket, MarkerViewState?> {
    override fun create(data: CapturedPacket): MarkerViewState = DummyMarker.also {
        assertNotNull((data.parsed.data as Mapable).coordinates)
    }
}
