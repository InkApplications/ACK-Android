package com.inkapplications.ack.android.station

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.AprsLogItem
import com.inkapplications.ack.android.ui.*
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun StationScreen(
    viewState: StationViewState,
    createMapView: (Context) -> View,
    controller: StationScreenController,
) {
    if (viewState is StationViewState.Loaded) {
        StationDetails(viewState, createMapView, controller)
    }
}

@Composable
private fun StationDetails(
    viewState: StationViewState.Loaded,
    createMapView: (Context) -> View,
    controller: StationScreenController,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        if (viewState.insight.markers.isNotEmpty()) {
            Column {
                Box {
                    AndroidView(
                        factory = createMapView,
                        modifier = Modifier.aspectRatio(16f / 9f),
                    )
                    IconButton(
                        onClick = controller::onBackPressed
                    ) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.navigate_up))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = AckTheme.spacing.gutter)) {
                    Text(
                        viewState.insight.name,
                        style = AckTheme.typography.h1,
                        modifier = Modifier.padding(
                            start = AckTheme.spacing.gutter,
                            end = AckTheme.spacing.gutter,
                        ),
                    )
                }
            }
        } else {
            Box(
                Modifier.padding(
                    start = AckTheme.spacing.gutter,
                    top = AckTheme.spacing.gutter,
                    end = AckTheme.spacing.gutter,
                )
            ) {
                NavigationRow(
                    title = {
                        Text(
                            viewState.insight.name,
                            style = AckTheme.typography.h1,
                            modifier = Modifier.padding(
                                start = AckTheme.spacing.gutter,
                                end = AckTheme.spacing.gutter,
                            ),
                        )
                    },
                    onBackPressed = controller::onBackPressed
                )
            }
        }
        Column(
            modifier = Modifier.padding(
                top = AckTheme.spacing.content,
                start = AckTheme.spacing.gutter,
                end = AckTheme.spacing.gutter,
                bottom = AckTheme.spacing.gutter,
            ),
        ) {
            if (viewState.insight.temperature != null) {
                IconRow(Icons.Default.WbSunny, viewState.insight.temperature)
            }
            if (viewState.insight.wind != null) {
                IconRow(Icons.Default.Air, viewState.insight.wind)
            }
            if (viewState.insight.altitude != null) {
                IconRow(Icons.Default.Terrain, viewState.insight.altitude)
            }
            if (viewState.insight.comment != null) {
                IconRow(Icons.Default.Comment, viewState.insight.comment)
            }
            if (viewState.insight.telemetryValues != null) {
                TelemetryTable(viewState.insight.telemetryValues, viewState.insight.telemetrySequence)
            }
            Text(
                text = stringResource(R.string.station_packet_list_title),
                style = AckTheme.typography.h2,
                modifier = Modifier.padding(vertical = AckTheme.spacing.content),
            )
            viewState.packets.forEach { log ->
                AprsLogItem(log, controller::onLogItemClicked)
            }
        }
    }
}
