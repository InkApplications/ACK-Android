package com.inkapplications.ack.android.station

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
import com.inkapplications.ack.android.ui.*
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun StationScreen(
    viewState: StationViewModel,
    createMapView: (Context) -> View,
    onBackPressed: () -> Unit,
) {
    Column {
        if (viewState.mapVisible) {
            Column {
                Box {
                    AndroidView(
                        factory = createMapView,
                        modifier = Modifier.aspectRatio(16f / 9f),
                    )
                    IconButton(
                        onClick = onBackPressed
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
                            end = AckTheme.dimensions.gutter,
                        ),
                    )
                    if (viewState.receiveIcon != null) {
                        Icon(viewState.receiveIcon, viewState.receiveIconDescription)
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
                                end = AckTheme.dimensions.gutter,
                            ),
                        )
                        if (viewState.receiveIcon != null) {
                            Icon(viewState.receiveIcon, viewState.receiveIconDescription)
                        }
                    },
                    onBackPressed = onBackPressed
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
            if (viewState.temperatureVisible) {
                IconRow(Icons.Default.WbSunny, viewState.temperature)
            }
            if (viewState.windVisible) {
                IconRow(Icons.Default.Air, viewState.wind)
            }
            if (viewState.altitudeVisible) {
                IconRow(Icons.Default.Terrain, viewState.altitude)
            }
            if (viewState.commentVisible) {
                IconRow(Icons.Default.Comment, viewState.comment)
            }

            if (viewState.telemetryValues != null) {
                TelemetryTable(viewState.telemetryValues, viewState.telemetrySequence)
            }

            if (viewState.debugDataVisible) {
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
