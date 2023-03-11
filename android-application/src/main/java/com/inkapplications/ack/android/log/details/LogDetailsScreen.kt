package com.inkapplications.ack.android.log.details

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
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
import com.inkapplications.ack.android.ui.IconRow
import com.inkapplications.ack.android.ui.NavigationRow
import com.inkapplications.ack.android.ui.TelemetryTable
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun LogDetailsScreen(
    viewState: LogDetailsState,
    controller: LogDetailsController,
    createMapView: (Context) -> View,
) {
    if (viewState !is LogDetailsState.Loaded) return

    Column {
        if (viewState.markers.isNotEmpty()) {
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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = AckTheme.dimensions.gutter)) {
                    Text(
                        viewState.name,
                        style = AckTheme.typography.h1,
                        modifier = Modifier.padding(
                            start = AckTheme.dimensions.gutter,
                        ),
                    )
                    IconButton(
                        onClick = { controller.onViewStationDetails(viewState.callsign) },
                    ) {
                        Icon(Icons.Default.Info, stringResource(R.string.capture_log_info))
                    }
                }
            }
        } else {
            Box(
                Modifier.padding(
                    start = AckTheme.dimensions.gutter,
                    top = AckTheme.dimensions.gutter,
                    end = AckTheme.dimensions.gutter,
                )
            ) {
                NavigationRow(
                    title = {
                        Text(
                            viewState.name,
                            style = AckTheme.typography.h1,
                            modifier = Modifier.padding(
                                start = AckTheme.dimensions.gutter,
                            ),
                        )
                        IconButton(
                            onClick = { controller.onViewStationDetails(viewState.callsign) },
                        ) {
                            Icon(Icons.Default.Info, stringResource(R.string.capture_log_info))
                        }
                    },
                    onBackPressed = controller::onBackPressed
                )
            }
        }
        Column(
            modifier = Modifier.padding(
                top = AckTheme.dimensions.content,
                start = AckTheme.dimensions.gutter,
                end = AckTheme.dimensions.gutter,
                bottom = AckTheme.dimensions.gutter,
            ),
        ) {
            IconRow(
                icon = viewState.receiveIcon,
                viewState.timestamp,
            )
            if (viewState.temperature != null) {
                IconRow(Icons.Default.WbSunny, viewState.temperature)
            }
            if (viewState.wind != null) {
                IconRow(Icons.Default.Air, viewState.wind)
            }
            if (viewState.altitude != null) {
                IconRow(Icons.Default.Terrain, viewState.altitude)
            }
            if (viewState.comment != null) {
                IconRow(Icons.Default.Comment, viewState.comment)
            }
            if (viewState.telemetryValues != null) {
                TelemetryTable(viewState.telemetryValues, viewState.telemetrySequence)
            }
            if (viewState.rawSource != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = AckTheme.dimensions.content)) {
                    Column(modifier = Modifier.padding(AckTheme.dimensions.content)) {
                        Text("Debug Info", style = AckTheme.typography.h2)
                        Text("Raw Data", style = AckTheme.typography.h3)
                        Text(viewState.rawSource, style = AckTheme.typography.caption)
                        Spacer(Modifier.height(AckTheme.dimensions.content))
                    }
                }
            }
        }
    }
}
