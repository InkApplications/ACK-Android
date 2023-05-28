package com.inkapplications.ack.android.onboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.inkapplications.ack.android.settings.license.LicensePromptFieldValues
import com.inkapplications.ack.android.settings.license.LicensePromptScreen
import com.inkapplications.ack.android.settings.license.LicensePromptValidator
import com.inkapplications.ack.android.ui.theme.AckScreen

@Composable
fun OnboardScreen(
    viewModel: OnboardingViewModel,
    userAgreementController: UserAgreementController,
    licenseValidator: LicensePromptValidator,
    onLicenseContinueClick: (LicensePromptFieldValues) -> Unit,
) = AckScreen {
    val state = viewModel.screenState.collectAsState()
    when (val onboardingState = state.value) {
        OnboardingState.Complete -> {}
        OnboardingState.Initial -> {}
        is OnboardingState.LicensePrompt -> LicensePromptScreen(
            initialValues = onboardingState.initialValues,
            validator = licenseValidator,
            onContinue = onLicenseContinueClick,
        )
        OnboardingState.UserAgreement -> UsageAgreementPrompt(userAgreementController)
    }
}
