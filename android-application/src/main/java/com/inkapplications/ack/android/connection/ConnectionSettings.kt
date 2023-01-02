package com.inkapplications.ack.android.connection

import com.inkapplications.ack.android.settings.*
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.settings.transformer.MileTransformer
import com.inkapplications.ack.android.settings.transformer.OptionalKeyTransformer
import com.inkapplications.ack.android.settings.transformer.StationAddressTransformer
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.data.*
import dagger.Reusable
import inkapplications.spondee.measure.us.miles
import javax.inject.Inject

@Reusable
class ConnectionSettings @Inject constructor(
    resources: StringResources
): SettingsProvider {
    val address = StringBackedSetting(
        key = "connection.address",
        name = resources.getString(R.string.connection_setting_address_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = null,
        advanced = true,
        transformer = OptionalKeyTransformer(
            nullKey = "",
            delegate = StationAddressTransformer,
        )
    )
    val passcode = IntSetting(
        key = "connection.passcode",
        name = resources.getString(R.string.connection_setting_passcode_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = -1,
        advanced = true,
    )

    val server = StringSetting(
        key = "connection.server",
        name = resources.getString(R.string.connection_setting_server_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = DEFAULT_CONNECTION_SERVER,
    )
    val port = IntSetting(
        key = "connection.port",
        name = resources.getString(R.string.connection_setting_port_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = DEFAULT_CONNECTION_PORT,
    )
    val radius = IntBackedSetting(
        key = "connection.radius",
        name = resources.getString(R.string.connection_setting_radius_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = DEFAULT_SEARCH_RADIUS_MILES.miles,
        transformer = MileTransformer,
    )

    override val settings: List<Setting> = listOf(
        address,
        passcode,
        server,
        port,
        radius,
    )
}
