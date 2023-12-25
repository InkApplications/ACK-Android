package com.inkapplications.ack.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.settings.buildinfo.BuildDataAccess
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoFactory
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Android Viewmodel for storing the state of the settings screen.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    settingsAccess: SettingsAccess,
    settingsViewStateFactory: SettingsViewStateFactory,
    buildDataAccess: BuildDataAccess,
    buildInfoFactory: BuildInfoFactory,
): ViewModel() {
    private val visibility = MutableStateFlow(SettingVisibility.Visible)

    val settingsList = visibility.flatMapLatest { visibility ->
        settingsAccess.settingsGroups(visibility)
    }.map {
        settingsViewStateFactory.viewState(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsListViewState.Initial)

    val licenseState = settingsAccess.licenseData
        .map { settingsViewStateFactory.licenseState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LicenseViewState.Initial)

    val buildInfoState: StateFlow<BuildInfoState> = buildDataAccess.buildData
        .map { buildInfoFactory.buildInfo(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BuildInfoState.Initial)

    fun showAdvanced(value: Boolean) {
        visibility.value = if (value) {
            SettingVisibility.Advanced
        } else {
            SettingVisibility.Visible
        }
    }

    fun showDev() {
        visibility.getAndUpdate {
            if (it == SettingVisibility.Advanced) SettingVisibility.Dev else it
        }
    }
}

