package com.inkapplications.ack.android.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.AprsLogItem
import com.inkapplications.ack.android.log.LogItemViewState
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.data.CaptureId

@Composable
fun MapScreen(
    state: MapViewState,
    onMapItemClick: (CaptureId?) -> Unit,
    onLogItemClick: (LogItemViewState) -> Unit,
    onEnableLocation: () -> Unit,
    onDisableLocation: () -> Unit,
    bottomContentProtection: Dp,
) = AckScreen {
    MarkerMap(
        viewModel = state.mapViewModel,
        onMapItemClicked = onMapItemClick,
        bottomProtection = dimensionResource(R.dimen.mapbox_logo_padding_bottom),
        modifier = Modifier.fillMaxSize()
    )
    val logState = state.selectedItem
    if (state.selectedItemVisible && logState != null) {
        Row (
            modifier = Modifier
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .padding(top = AckTheme.spacing.gutter)
        ) {
            AprsLogItem(
                log = logState,
                onClick = onLogItemClick,
                border = true,
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = bottomContentProtection),
        contentAlignment = Alignment.BottomEnd,
    ) {
        LocationStateButton(state.mapViewModel.enablePositionTracking, onEnableLocation, onDisableLocation)
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
