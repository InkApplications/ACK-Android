package com.inkapplications.ack.android.settings.license

import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.settings.SettingsAccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android viewmodel containing the current state of the license edit screen.
 */
@HiltViewModel
class LicenseEditViewModel @Inject constructor(
    settingsAccess: SettingsAccess,
): androidx.lifecycle.ViewModel() {
    val state = settingsAccess.licensePromptFieldValues
        .map { LicenseEditState.Editable(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LicenseEditState.Initial)
}
