package com.inkapplications.ack.android.settings.license

import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.settings.SettingsAccess
import com.inkapplications.android.extensions.ExtendedActivity
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kimchi.analytics.Property
import javax.inject.Inject

/**
 * Displays an edit-form for editing the user's licens after onboarding.
 */
@AndroidEntryPoint
class LicenseEditActivity: ExtendedActivity() {
    @Inject
    lateinit var settingsAccess: SettingsAccess

    override fun onCreate() {
        super.onCreate()
        setContent {
            val viewModel: LicenseEditViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState().value

            when (state) {
                LicenseEditState.Initial -> {}
                is LicenseEditState.Editable -> LicensePromptScreen(
                    initialValues = state.initialValues,
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
