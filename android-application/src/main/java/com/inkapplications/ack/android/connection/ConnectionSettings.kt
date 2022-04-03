package com.inkapplications.ack.android.connection

import com.inkapplications.ack.android.settings.*
import com.inkapplications.ack.android.settings.transformer.Transformer
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.data.*
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.ack.structures.station.toStationAddress
import dagger.Reusable
import javax.inject.Inject

@Reusable
class ConnectionSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val address = StringBackedSetting(
        key = "connection.address",
        name = "Callsign",
        categoryName = "Connection",
        defaultData = null,
        advanced = true,
        transformer = object: Transformer<StationAddress?, String> {
            override fun toStorage(data: StationAddress?): String = data.toString()
            override fun toData(storage: String): StationAddress? = storage.takeIf { it.isNotEmpty() }?.toStationAddress()
        },
    )
    val passcode = IntSetting(
        key = "connection.passcode",
        name = "APRS-IS Passcode",
        categoryName = "Connection",
        defaultValue = -1,
        advanced = true,
    )

    val server = StringSetting(
        key = "connection.server",
        name = "APRS-IS Server",
        categoryName = "Connection",
        defaultValue = DEFAULT_CONNECTION_SERVER,
    )
    val port = IntSetting(
        key = "connection.port",
        name = "APRS-IS Port",
        categoryName = "Connection",
        defaultValue = DEFAULT_CONNECTION_PORT,
    )
    val radius = IntSetting(
        key = "connection.radius",
        name = "APRS-IS Search Radius (km)",
        categoryName = "Connection",
        defaultValue = DEFAULT_SEARCH_RADIUS_KILOMETERS,
    )

    override val settings: List<Setting> = listOf(
        address,
        passcode,
        server,
        port,
        radius,
    )
}
