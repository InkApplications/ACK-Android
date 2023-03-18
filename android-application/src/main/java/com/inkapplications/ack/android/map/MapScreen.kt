package com.inkapplications.ack.android.map

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.ack.android.log.AprsLogItem
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.android.R

@Composable
fun MapScreen(
    state: MapViewState,
    mapFactory: (Context) -> View,
    onLogItemClick: (LogItemViewState) -> Unit,
    onEnableLocation: () -> Unit,
    onDisableLocation: () -> Unit,
    bottomContentProtection: Dp,
) = AckScreen {
    AndroidView(
        factory = mapFactory,
        modifier = Modifier.fillMaxSize()
    )
    val logState = state.selectedItem
    if (state.selectedItemVisible && logState != null) {
        Row (modifier = Modifier.padding(top = AckTheme.spacing.gutter)) {
            AprsLogItem(
                log = logState,
                onClick = onLogItemClick,
                border = true,
            )
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = bottomContentProtection),
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
            backgroundColor = AckTheme.colors.surface,
            contentColor = AckTheme.colors.accent,
            modifier = Modifier.padding(AckTheme.spacing.gutter)
        ) {
            Icon(Icons.Default.MyLocation, stringResource(R.string.map_location_tracking_stop_action))
        }
    } else {
        FloatingActionButton(
            onClick = onEnableTrackingClick,
            backgroundColor = AckTheme.colors.surface,
            contentColor = contentColorFor(AckTheme.colors.surface),
            modifier = Modifier.padding(AckTheme.spacing.gutter)
        ) {
            Icon(Icons.Default.LocationDisabled, stringResource(R.string.map_location_tracking_start_action))
        }
    }
}
