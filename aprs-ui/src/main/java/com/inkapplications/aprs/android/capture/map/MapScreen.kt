package com.inkapplications.aprs.android.capture.map

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.aprs.android.capture.log.AprsLogItem
import com.inkapplications.aprs.android.capture.log.LogItemState
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme

@Composable
fun MapScreen(
    state: MapViewModel,
    mapFactory: (Context) -> View,
    onLogItemClick: (LogItemState) -> Unit,
    onEnableLocation: () -> Unit,
    onDisableLocation: () -> Unit,
) = AprsScreen {
    AndroidView(
        factory = mapFactory,
        modifier = Modifier.fillMaxSize()
    )
    val logState = state.selectedItem?.viewModel
    if (state.selectedItemVisible && logState != null) {
        Row {
            AprsLogItem(
                log = logState,
                onClick = onLogItemClick,
            )
        }
    }
    Box(
        contentAlignment = Alignment.BottomEnd,
    ) {
        LocationStateButton(state.trackPosition, onEnableLocation, onDisableLocation)
    }
}

@Composable
fun LocationStateButton(
    isTracking: Boolean,
    onEnableTrackingClick: () -> Unit,
    onDisableTrackingClick: () -> Unit
) {
    if (isTracking) {
        FloatingActionButton(
            onClick = onDisableTrackingClick,
            Modifier.padding(AprsTheme.Spacing.gutter)
        ) {
            Icon(Icons.Default.MyLocation, "")
        }
    } else {
        FloatingActionButton(
            onClick = onEnableTrackingClick,
            Modifier.padding(AprsTheme.Spacing.gutter)
        ) {
            Icon(Icons.Default.LocationDisabled, "")
        }
    }
    LinearProgressIndicator()
}
