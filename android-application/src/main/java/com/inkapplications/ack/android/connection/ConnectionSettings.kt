package com.inkapplications.ack.android.connection

import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.android.settings.IntSetting
import com.inkapplications.ack.android.settings.Setting
import com.inkapplications.ack.android.settings.SettingsProvider
import com.inkapplications.ack.android.settings.StringSetting
import com.inkapplications.ack.data.*
import dagger.Reusable
import javax.inject.Inject

@Reusable
class ConnectionSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val callsign = StringSetting(
        key = "connection.callsign",
        name = "Callsign",
        categoryName = "Connection",
        defaultValue = "",
        advanced = true,
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
        callsign,
        passcode,
        server,
        port,
        radius,
    )
}
