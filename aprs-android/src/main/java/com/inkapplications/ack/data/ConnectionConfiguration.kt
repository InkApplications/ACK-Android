package com.inkapplications.ack.data

import com.inkapplications.ack.client.Credentials
import com.inkapplications.ack.structures.station.StationAddress
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.us.miles

const val DEFAULT_CONNECTION_SERVER = "rotate.aprs2.net"
const val DEFAULT_CONNECTION_PORT = 14580
const val DEFAULT_SEARCH_RADIUS_MILES = 100

data class ConnectionConfiguration(
    val address: StationAddress,
    val passcode: Int = -1,
    val host: String = DEFAULT_CONNECTION_SERVER,
    val port: Int = DEFAULT_CONNECTION_PORT,
    val searchRadius: Length = DEFAULT_SEARCH_RADIUS_MILES.miles,
) {
    val credentials = Credentials(address.toString(), passcode)
}
