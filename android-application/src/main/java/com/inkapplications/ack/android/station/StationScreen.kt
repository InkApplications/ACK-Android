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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.*
import com.inkapplications.ack.android.ui.theme.AprsTheme

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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = AprsTheme.spacing.gutter)) {
                    Text(
                        viewState.name,
                        style = AprsTheme.typography.h1,
                        modifier = Modifier.padding(
                            start = AprsTheme.spacing.gutter,
                            end = AprsTheme.spacing.gutter,
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
                    start = AprsTheme.spacing.gutter,
                    top = AprsTheme.spacing.gutter,
                    end = AprsTheme.spacing.gutter,
                )
            ) {
                NavigationRow(
                    title = {
                        Text(
                            viewState.name,
                            style = AprsTheme.typography.h1,
                            modifier = Modifier.padding(
                                start = AprsTheme.spacing.gutter,
                                end = AprsTheme.spacing.gutter,
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
                top = AprsTheme.spacing.content,
                start = AprsTheme.spacing.gutter,
                end = AprsTheme.spacing.gutter,
                bottom = AprsTheme.spacing.gutter,
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
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = AprsTheme.spacing.content)) {
                    Column(modifier = Modifier.padding(AprsTheme.spacing.content)) {
                        Text("Debug Info", style = AprsTheme.typography.h2)
                        Text("Raw Data", style = AprsTheme.typography.h3)
                        Text(viewState.rawSource, style = AprsTheme.typography.caption)
                        Spacer(Modifier.height(AprsTheme.spacing.content))
                    }
                }
            }
        }
    }
}
