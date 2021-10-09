package com.inkapplications.aprs.android.onboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.inkapplications.aprs.android.settings.license.LicensePromptScreen
import com.inkapplications.aprs.android.settings.license.LicensePromptValidator
import com.inkapplications.aprs.android.ui.theme.AprsScreen

@Composable
fun OnboardScreen(
    state: State<OnboardingState>,
    userAgreementController: UserAgreementController,
    initialLicensePromptFieldValues: LicensePromptFieldValues,
    licenseValidator: LicensePromptValidator,
    onLicenseContinueClick: (LicensePromptFieldValues) -> Unit,
) = AprsScreen {
    when {
        state.value.agreementRequired -> UsageAgreement(userAgreementController)
        state.value.licensePromptRequired -> LicensePromptScreen(
            initialValues = initialLicensePromptFieldValues,
            validator = licenseValidator,
            onContinue = onLicenseContinueClick,
        )
    }
}
