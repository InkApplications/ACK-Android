package com.inkapplications.aprs.android.onboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.inkapplications.aprs.android.ui.AprsScreen

@Composable
fun OnboardScreen(
    state: State<OnboardingState>,
    onAgreeClick: () -> Unit,
    onDeclineClick: () -> Unit,
    onLicenseContinueClick: (String, String) -> Unit,
) = AprsScreen {
    when {
        state.value.agreementRequired -> UsageAgreement(
            onDeclineClick = onDeclineClick,
            onAgreeClick = onAgreeClick,
        )
        state.value.licensePromptRequired -> LicensePrompt(
            onContinue = onLicenseContinueClick,
        )
    }
}
