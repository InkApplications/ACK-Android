package com.inkapplications.ack.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.settings.buildinfo.BuildDataAccess
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoFactory
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val advanced = MutableStateFlow(false)

    val settingsList = advanced.flatMapLatest { advanced ->
        settingsAccess.settingsGroups(advanced)
    }.map {
        settingsViewStateFactory.viewState(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsListViewState.Initial)

    val licenseState = settingsAccess.licenseData
        .map { settingsViewStateFactory.licenseState(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, LicenseViewState.Initial)

    val buildInfoState: StateFlow<BuildInfoState> = buildDataAccess.buildData
        .map { buildInfoFactory.buildInfo(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BuildInfoState.Initial)

    fun showAdvanced() {
        advanced.value = true
    }
}

