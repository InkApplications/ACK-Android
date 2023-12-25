package com.inkapplications.ack.android.connection

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.IntegerValidator
import com.inkapplications.ack.android.input.RegexValidator
import com.inkapplications.ack.android.input.enumInputValidator
import com.inkapplications.ack.android.settings.*
import com.inkapplications.ack.android.settings.transformer.*
import com.inkapplications.ack.data.DEFAULT_CONNECTION_PORT
import com.inkapplications.ack.data.DEFAULT_CONNECTION_SERVER
import com.inkapplications.ack.data.DEFAULT_SEARCH_RADIUS_MILES
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import inkapplications.spondee.measure.us.miles
import javax.inject.Inject

@Reusable
class ConnectionSettings @Inject constructor(
    resources: StringResources,
): SettingsProvider {
    private val callsignRegex = Regex("^[0-9a-zA-Z]{1,3}\\d[0-9a-zA-Z]{0,4}[a-zA-Z](?:-[a-zA-Z0-9]{1,2})?\$")
    private val hostnameRegex = Regex("^(?=.{1,255}\$)[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?(?:\\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|-){0,61}[0-9A-Za-z])?)*\\.?\$")

    val address = StringBackedSetting(
        key = "connection.address",
        name = resources.getString(R.string.connection_setting_address_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = null,
        visibility = SettingVisibility.Dev,
        storageTransformer = SentinelOptionalTransformer(
            sentinelValue = "",
            delegate = StationAddressTransformer + TrimmingTransformer,
        ),
        inputValidator = RegexValidator(
            regex = callsignRegex,
            error = resources.getString(R.string.connection_setting_address_invalid),
        ),
    )

    val passcode = IntBackedSetting(
        key = "connection.passcode",
        name = resources.getString(R.string.connection_setting_passcode_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = null,
        visibility = SettingVisibility.Dev,
        storageTransformer = SentinelOptionalTransformer(
            sentinelValue = -1,
            delegate = object: Transformer<Passcode, Int> {
                override fun toStorage(data: Passcode): Int = data.value
                override fun toData(storage: Int): Passcode = storage.let(::Passcode)
            },
        ),
        inputValidator = IntegerValidator(
            error = resources.getString(R.string.connection_setting_passcode_invalid),
            allowSentinel = -1,
        ),
        fieldTransformer = OptionalIntTransformer(),
    )

    val server = StringSetting(
        key = "connection.server",
        name = resources.getString(R.string.connection_setting_server_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = DEFAULT_CONNECTION_SERVER,
        visibility = SettingVisibility.Advanced,
        validator = RegexValidator(
            regex = hostnameRegex,
            error = resources.getString(R.string.connection_setting_server_invalid),
        ),
    )
    val port = IntSetting(
        key = "connection.port",
        name = resources.getString(R.string.connection_setting_port_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = DEFAULT_CONNECTION_PORT,
        visibility = SettingVisibility.Advanced,
        validator = IntegerValidator(
            error = resources.getString(R.string.connection_setting_port_invalid),
            zeroInclusive = false,
        ),
    )

    val radius = IntBackedSetting(
        key = "connection.radius",
        name = resources.getString(R.string.connection_setting_radius_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = DEFAULT_SEARCH_RADIUS_MILES.miles,
        storageTransformer = MileTransformer,
        inputValidator = IntegerValidator(
            error = resources.getString(R.string.connection_setting_radius_invalid),
            zeroInclusive = false,
        ),
    )

    val driver = StringBackedSetting(
        key = "connection.driver",
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = DriverSelection.Audio,
        name = resources.getString(R.string.transmit_settings_driver),
        visibility = SettingVisibility.Dev,
        storageTransformer = enumTransformer(),
        inputValidator = enumInputValidator<DriverSelection>(
            error = resources.getString(
                R.string.settings_validation_not_in_set,
                DriverSelection.values().joinToString { it.name },
            ),
        ),
    )

    override val settings: List<Setting> = listOf(
        address,
        passcode,
        server,
        port,
        radius,
        driver,
    )
}
