package com.inkapplications.aprs.android.onboard

import org.junit.Test
import kotlin.test.assertEquals

class LicenseFormatterTest {
    @Test
    fun passcodeTest() {
        assertEquals(24790, LicenseValidator.getPasscode("K4X"))
        assertEquals(24790, LicenseValidator.getPasscode("K4X-17"))
        assertEquals(28817, LicenseValidator.getPasscode("B2AA"))
        assertEquals(25296, LicenseValidator.getPasscode("I20000X"))
    }
}
