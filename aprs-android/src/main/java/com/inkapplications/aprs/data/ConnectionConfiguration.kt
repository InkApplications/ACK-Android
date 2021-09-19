package com.inkapplications.aprs.data

import com.inkapplications.karps.client.Credentials
import com.inkapplications.karps.structures.Address
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.of

const val DEFAULT_CONNECTION_SERVER = "rotate.aprs2.net"
const val DEFAULT_CONNECTION_PORT = 14580
const val DEFAULT_SEARCH_RADIUS_KILOMETERS = 100

data class ConnectionConfiguration(
    val address: Address,
    val passcode: Int = -1,
    val host: String = DEFAULT_CONNECTION_SERVER,
    val port: Int = DEFAULT_CONNECTION_PORT,
    val searchRadius: Length = Meters.of(Kilo, DEFAULT_SEARCH_RADIUS_KILOMETERS),
) {
    val credentials = Credentials(address.callsign, passcode)
}
