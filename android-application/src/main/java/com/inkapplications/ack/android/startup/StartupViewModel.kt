package com.inkapplications.ack.android.startup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inkapplications.ack.android.settings.buildinfo.BuildDataAccess
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoFactory
import com.inkapplications.ack.android.settings.buildinfo.BuildInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Android viewmodel for storing the state loaded on the startup screen.
 */
@HiltViewModel
class StartupViewModel @Inject constructor(
    buildDataAccess: BuildDataAccess,
    buildInfoFactory: BuildInfoFactory,
): ViewModel() {
    val buildInfoState: StateFlow<BuildInfoState> = buildDataAccess.buildData
        .map { buildInfoFactory.buildInfo(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BuildInfoState.Initial)
}
