package com.inkapplications.ack.android.settings.license

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.connection.ConnectionSettings
import com.inkapplications.ack.android.input.ConvertibleOptionalIntValidator
import com.inkapplications.ack.android.input.ValidationResult
import com.inkapplications.ack.client.generatePasscode
import com.inkapplications.android.extensions.StringResources
import dagger.Reusable
import kimchi.logger.KimchiLogger
import javax.inject.Inject

@Reusable
class LicensePromptValidator @Inject constructor(
    private val connectionSettings: ConnectionSettings,
    private val intValidator: ConvertibleOptionalIntValidator,
    private val stringResources: StringResources,
    private val logger: KimchiLogger,
) {
    fun getLicenseError(license: String): String? {
        return connectionSettings.address.validator
            .validate(license)
            .let { (it as? ValidationResult.Error)?.message }
    }

    fun getPasscodeError(license: String, passcode: String): String? {
        val fieldValidation = intValidator.validate(passcode)
        if (fieldValidation is ValidationResult.Error) return fieldValidation.message
        
        val converted = connectionSettings.passcode.fieldTransformer.toStorage(passcode)
        val inputValidation = connectionSettings.passcode.inputValidator.validate(converted)
        val data = connectionSettings.passcode.storageTransformer.toData(converted)
        val actual = generatePasscode(license)

        return when {
            inputValidation is ValidationResult.Error -> inputValidation.message.also {
                logger.debug("Passcode input validation failed")
            }
            data == null -> null
            converted != actual -> stringResources.getString(R.string.connection_setting_passcode_incorrect)
            else -> null
        }
    }

    fun isValid(license: String, passcode: String): Boolean {
        return getLicenseError(license) == null && getPasscodeError(license, passcode) == null
    }
}

