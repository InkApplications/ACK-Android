package com.inkapplications.ack.android.settings.license

import com.inkapplications.ack.client.generatePasscode
import dagger.Reusable
import javax.inject.Inject

@Reusable
class LicensePromptValidator @Inject constructor() {
    private val callsignRegex = Regex("^[0-9a-zA-Z]{1,3}\\d[0-9a-zA-Z]{0,4}[a-zA-Z](?:-[a-zA-Z0-9]{1,2})?\$")

    fun getLicenseError(license: String): String? {
        return when {
            license.isBlank() -> null
            license.trim().matches(callsignRegex) -> null
            else -> "Invalid Callsign"
        }
    }

    fun getPasscodeError(license: String, passcode: String): String? {
        val actual = generatePasscode(license)
        return when {
            passcode.isBlank() -> null
            actual == passcode.trim().toIntOrNull() -> null
            else -> "Invalid Passcode"
        }
    }

    fun isValid(license: String, passcode: String): Boolean {
        return getLicenseError(license) == null && getPasscodeError(license, passcode) == null
    }
}
