package com.inkapplications.ack.android.onboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android viewmodel containing the current state of an onboarding screen.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    stateAccess: OnboardingStateAccess,
    onboardingStateFactory: OnboardingStateFactory,
): ViewModel() {
    val screenState = stateAccess.onboardingData
        .map { onboardingStateFactory.screenState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, OnboardingState.Initial)
}
