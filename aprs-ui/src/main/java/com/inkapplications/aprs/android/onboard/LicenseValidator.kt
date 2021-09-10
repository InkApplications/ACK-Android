package com.inkapplications.aprs.android.onboard

object LicenseValidator {
    private val callsignRegex = Regex("^[0-9a-zA-Z]{1,3}\\d[0-9a-zA-Z]{0,4}[a-zA-Z](?:-[a-zA-Z0-9]{1,2})?\$")

    fun validateCallsign(callsign: String) = callsign.matches(callsignRegex)
    fun validatePasscode(address: String, passcode: Int) = passcode == getPasscode(address)

    fun getPasscode(address: String): Int {
        var hash = 0x73e2
        address
            .substringBefore('-')
            .uppercase()
            .toCharArray()
            .forEachIndexed { index, c ->
                if (index % 2 == 0) {
                    hash = hash.xor(c.code.shl(8))
                } else {
                    hash = hash.xor(c.code)
                }
            }

        return hash.and(0x7fff)
    }
}
