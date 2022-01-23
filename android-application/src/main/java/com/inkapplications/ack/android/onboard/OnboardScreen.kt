package com.inkapplications.ack.android.onboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.inkapplications.ack.android.settings.license.LicensePromptScreen
import com.inkapplications.ack.android.settings.license.LicensePromptValidator
import com.inkapplications.ack.android.ui.theme.AprsScreen

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
