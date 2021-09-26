package com.inkapplications.aprs.android.settings.license

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.inkapplications.android.extensions.ExtendedActivity
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.onboard.LicensePromptFieldValues
import com.inkapplications.aprs.android.settings.SettingsAccess
import kimchi.Kimchi
import kimchi.analytics.Property

class LicenseEditActivity: ExtendedActivity() {
    private lateinit var settingsAccess: SettingsAccess

    override fun onCreate() {
        super.onCreate()
        settingsAccess = component.settingsAccess()
        setContent {
            val fieldState = settingsAccess.licensePromptFieldValues.collectAsState(null).value
            if (fieldState != null) {
                LicensePromptScreen(
                    initialValues = fieldState,
                    validator = LicensePromptValidator(),
                    onContinue = ::onContinue
                )
            }
        }
    }

    private fun onContinue(values: LicensePromptFieldValues) {
        Kimchi.trackEvent("license_update", listOf(
            Property.IntProperty("license_callsign_provided", if (values.callsign.isBlank()) 0 else 1),
            Property.IntProperty("license_passcode_provided", if (values.passcode.isBlank()) 0 else 1),
        ))
        settingsAccess.setLicense(values)
        finish()
    }
}
