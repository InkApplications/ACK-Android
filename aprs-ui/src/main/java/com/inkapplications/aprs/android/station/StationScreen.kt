package com.inkapplications.aprs.android.station

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Comment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.ui.*

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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = AprsTheme.Spacing.gutter)) {
                    Text(
                        viewState.name,
                        style = AprsTheme.Typography.h1,
                        modifier = Modifier.padding(
                            start = AprsTheme.Spacing.gutter,
                            end = AprsTheme.Spacing.gutter,
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
                    start = AprsTheme.Spacing.gutter,
                    top = AprsTheme.Spacing.gutter,
                    end = AprsTheme.Spacing.gutter,
                )
            ) {
                NavigationRow(
                    title = {
                        Text(
                            viewState.name,
                            style = AprsTheme.Typography.h1,
                            modifier = Modifier.padding(
                                start = AprsTheme.Spacing.gutter,
                                end = AprsTheme.Spacing.gutter,
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
                top = AprsTheme.Spacing.content,
                start = AprsTheme.Spacing.gutter,
                end = AprsTheme.Spacing.gutter,
                bottom = AprsTheme.Spacing.gutter,
            ),
        ) {
            if (viewState.temperatureVisible) {
                IconRow(painterResource(R.drawable.ic_weather), viewState.temperature)
            }
            if (viewState.windVisible) {
                IconRow(painterResource(R.drawable.ic_wind), viewState.wind)
            }
            if (viewState.altitudeVisible) {
                IconRow(painterResource(R.drawable.ic_altitude), viewState.altitude)
            }
            if (viewState.commentVisible) {
                IconRow(Icons.Default.Comment, viewState.comment)
            }

            if (viewState.telemetryValues != null) {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = AprsTheme.Spacing.content)) {
                    Column(modifier = Modifier.padding(AprsTheme.Spacing.content)) {
                        Text("Telemetry Data", style = AprsTheme.Typography.h2)
                        TelemetryValueRow("a1", viewState.telemetryValues.analog1.toString())
                        TelemetryValueRow("a2", viewState.telemetryValues.analog2.toString())
                        TelemetryValueRow("a3", viewState.telemetryValues.analog3.toString())
                        TelemetryValueRow("a4", viewState.telemetryValues.analog4.toString())
                        TelemetryValueRow("a5", viewState.telemetryValues.analog5.toString())
                        TelemetryValueRow("d1", viewState.telemetryValues.digital.toString())
                        TelemetryValueRow("sq", viewState.telemetrySequence.toString())
                    }
                }
            }

            if (viewState.debugDataVisible) {
                Card(modifier = Modifier.padding(vertical = AprsTheme.Spacing.content)) {
                    Column(modifier = Modifier.padding(AprsTheme.Spacing.content)) {
                        Text("Debug Info", style = AprsTheme.Typography.h2)
                        Text("Raw Data", style = AprsTheme.Typography.h3)
                        Text(viewState.rawPacket?.raw.toString(), style = AprsTheme.Typography.caption)
                        Spacer(Modifier.height(AprsTheme.Spacing.content))
                        Text("Parsing Info", style = AprsTheme.Typography.h3)
                        Text("Data Type Identifier: ${viewState.rawPacket?.dataTypeIdentifier}", style = AprsTheme.Typography.caption)
                        Text("Receive timestamp: ${viewState.rawPacket?.received}", style = AprsTheme.Typography.caption)
                        Text("Digipeater list: ${viewState.rawPacket?.digipeaters}", style = AprsTheme.Typography.caption)
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryValueRow(
    label: String,
    value: String,
) = KeyValueRow(label, value, Modifier.padding(vertical = AprsTheme.Spacing.singleItem))
