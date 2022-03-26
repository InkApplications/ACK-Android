package com.inkapplications.ack.data

import com.inkapplications.ack.client.Credentials
import com.inkapplications.ack.structures.station.StationAddress
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.of

const val DEFAULT_CONNECTION_SERVER = "rotate.aprs2.net"
const val DEFAULT_CONNECTION_PORT = 14580
const val DEFAULT_SEARCH_RADIUS_KILOMETERS = 100

data class ConnectionConfiguration(
    val address: StationAddress,
    val passcode: Int = -1,
    val host: String = DEFAULT_CONNECTION_SERVER,
    val port: Int = DEFAULT_CONNECTION_PORT,
    val searchRadius: Length = Meters.of(Kilo, DEFAULT_SEARCH_RADIUS_KILOMETERS),
) {
    val credentials = Credentials(address.toString(), passcode)
}
