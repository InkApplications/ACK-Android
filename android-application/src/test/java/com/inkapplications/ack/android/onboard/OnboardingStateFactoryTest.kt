package com.inkapplications.ack.android.onboard

import com.inkapplications.ack.android.settings.Passcode
import com.inkapplications.ack.structures.station.StationAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OnboardingStateFactoryTest {
    private val factory = OnboardingStateFactory()

    @Test
    fun agreementMismatch() {
        val data = OnboardingStateFactory.OnboardingData(
            latestRevision = 2,
            agreementRevision = 4,
        )

        val result = factory.screenState(data)

        assertTrue(result is OnboardingState.UserAgreement)
    }

    @Test
    fun licenseIncomplete() {
        val data = OnboardingStateFactory.OnboardingData(
            latestRevision = 2,
            agreementRevision = 2,
            completedLicense = false,
        )

        val result = factory.screenState(data)

        assertTrue(result is OnboardingState.LicensePrompt)
        assertEquals("", result.initialValues.callsign)
        assertEquals("", result.initialValues.passcode)
    }

    @Test
    fun licenseIncompleteWithData() {
        val data = OnboardingStateFactory.OnboardingData(
            latestRevision = 2,
            agreementRevision = 2,
            completedLicense = false,
            currentAddress = StationAddress("TEST", "7"),
            currentPasscode = Passcode(1234),
        )

        val result = factory.screenState(data)

        assertTrue(result is OnboardingState.LicensePrompt)
        assertEquals("TEST-7", result.initialValues.callsign)
        assertEquals("1234", result.initialValues.passcode)
    }

    @Test
    fun licenseComplete() {
        val data = OnboardingStateFactory.OnboardingData(
            latestRevision = 2,
            agreementRevision = 2,
            completedLicense = true,
        )

        val result = factory.screenState(data)

        assertTrue(result is OnboardingState.Complete)
    }
}
