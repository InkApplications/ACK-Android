package com.inkapplications.aprs.data

import com.inkapplications.karps.client.Credentials
import com.inkapplications.karps.structures.Address
import inkapplications.spondee.measure.Length
import inkapplications.spondee.measure.Meters
import inkapplications.spondee.structure.Kilo
import inkapplications.spondee.structure.of

data class ConnectionSettings(
    val address: Address,
    val passcode: Int = -1,
    val host: String = "rotate.aprs2.net",
    val port: Int = 14580,
    val searchRadius: Length = Meters.of(Kilo, 100),
) {
    val credentials = Credentials(address.callsign, passcode)
}
