package com.inkapplications.ack.android.connection

import com.inkapplications.ack.android.settings.*
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.input.OptionalIntValidator
import com.inkapplications.ack.android.input.ValidationResult
import com.inkapplications.ack.android.input.Validator
import com.inkapplications.ack.android.settings.transformer.MileTransformer
import com.inkapplications.ack.android.settings.transformer.OptionalIntTransformer
import com.inkapplications.ack.android.settings.transformer.OptionalKeyTransformer
import com.inkapplications.ack.android.settings.transformer.StationAddressTransformer
import com.inkapplications.ack.android.settings.transformer.Transformer
import com.inkapplications.ack.android.settings.transformer.TrimmingTransformer
import com.inkapplications.ack.android.settings.transformer.plus
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.ack.data.*
import dagger.Reusable
import inkapplications.spondee.measure.us.miles
import javax.inject.Inject

@Reusable
class ConnectionSettings @Inject constructor(
    resources: StringResources,
): SettingsProvider {
    private val callsignRegex = Regex("^[0-9a-zA-Z]{1,3}\\d[0-9a-zA-Z]{0,4}[a-zA-Z](?:-[a-zA-Z0-9]{1,2})?\$")

    val address = StringBackedSetting(
        key = "connection.address",
        name = resources.getString(R.string.connection_setting_address_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = null,
        visibility = SettingVisibility.Dev,
        storageTransformer = OptionalKeyTransformer(
            nullKey = "",
            delegate = StationAddressTransformer + TrimmingTransformer,
        ),
        inputValidator = object: Validator<String> {
            override fun validate(input: String): ValidationResult {
                return when {
                    input.trim().matches(callsignRegex) -> ValidationResult.Valid
                    else -> ValidationResult.Error(resources.getString(R.string.connection_setting_address_invalid))
                }
            }
        },
    )

    val passcode = IntBackedSetting(
        key = "connection.passcode",
        name = resources.getString(R.string.connection_setting_passcode_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = null,
        visibility = SettingVisibility.Dev,
        storageTransformer = OptionalKeyTransformer(
            nullKey = -1,
            delegate = object: Transformer<Passcode?, Int> {
                override fun toStorage(data: Passcode?): Int = data?.value ?: -1
                override fun toData(storage: Int): Passcode? = storage.takeIf { it != -1 }?.let(::Passcode)
            },
        ),
        inputValidator = object: Validator<Int> {
            override fun validate(input: Int): ValidationResult {
                return when {
                    input == -1 -> ValidationResult.Valid
                    input > 0 -> ValidationResult.Valid
                    else -> ValidationResult.Error(resources.getString(R.string.connection_setting_passcode_invalid))
                }
            }
        },
        fieldTransformer = OptionalIntTransformer(),
    )

    val server = StringSetting(
        key = "connection.server",
        name = resources.getString(R.string.connection_setting_server_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = DEFAULT_CONNECTION_SERVER,
        visibility = SettingVisibility.Advanced,
    )
    val port = IntSetting(
        key = "connection.port",
        name = resources.getString(R.string.connection_setting_port_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultValue = DEFAULT_CONNECTION_PORT,
        visibility = SettingVisibility.Advanced,
    )

    val radius = IntBackedSetting(
        key = "connection.radius",
        name = resources.getString(R.string.connection_setting_radius_name),
        categoryName = resources.getString(R.string.connection_setting_category_name),
        defaultData = DEFAULT_SEARCH_RADIUS_MILES.miles,
        storageTransformer = MileTransformer,
    )

    override val settings: List<Setting> = listOf(
        address,
        passcode,
        server,
        port,
        radius,
    )
}
