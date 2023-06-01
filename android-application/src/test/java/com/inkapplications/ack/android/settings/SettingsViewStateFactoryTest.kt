package com.inkapplications.ack.android.settings

import com.inkapplications.ack.structures.station.StationAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingsViewStateFactoryTest {
    private val factory = SettingsViewStateFactory()

    @Test
    fun registered() {
        val data = LicenseData(
            address = StationAddress("TEST", "7"),
            passcode = null,
        )

        val result = factory.licenseState(data)

        assertTrue(result is LicenseViewState.Registered)
        assertEquals("TEST-7", result.callsign)
    }

    @Test
    fun verified() {
        val data = LicenseData(
            address = StationAddress("TEST", "7"),
            passcode = Passcode(1234),
        )

        val result = factory.licenseState(data)

        assertTrue(result is LicenseViewState.Verified)
        assertEquals("TEST-7", result.callsign)
    }

    @Test
    fun unconfigured() {
        val data = LicenseData(
            address = null,
            passcode = null,
        )

        val result = factory.licenseState(data)

        assertTrue(result is LicenseViewState.Unconfigured)
    }
}
