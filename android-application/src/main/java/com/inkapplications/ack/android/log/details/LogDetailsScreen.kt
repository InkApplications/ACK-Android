package com.inkapplications.ack.android.log.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.map.MarkerMap
import com.inkapplications.ack.android.ui.IconRow
import com.inkapplications.ack.android.ui.NavigationRow
import com.inkapplications.ack.android.ui.TelemetryTable
import com.inkapplications.ack.android.ui.theme.AckTheme

/**
 * Detailed display of the data for a single logged packet.
 */
@Composable
fun LogDetailsScreen(
    viewModel: LogDetailsViewModel = hiltViewModel(),
    controller: LogDetailsController,
) {
    val viewState = viewModel.detailsState.collectAsState().value

    when (viewState) {
        is LogDetailsState.Initial -> {}
        is LogDetailsState.Loaded -> Details(viewState, controller)
    }
}

@Composable
private fun Details(
    viewState: LogDetailsState.Loaded,
    controller: LogDetailsController,
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
    ) {
        if (viewState.mapable) {
            Column {
                Box {
                    MarkerMap(
                        viewModel = viewState.mapViewState,
                        onMapItemClicked = controller::onMapItemClicked,
                        interactive = false,
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
                        viewState.name,
                        style = AckTheme.typography.h1,
                        modifier = Modifier.padding(
                            start = AckTheme.spacing.gutter,
                        ),
                    )
                    IconButton(
                        onClick = { controller.onViewStationDetails(viewState.callsign) },
                    ) {
                        Icon(Icons.Default.Info, stringResource(R.string.log_details_title))
                    }
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
                            viewState.name,
                            style = AckTheme.typography.h1,
                            modifier = Modifier.padding(
                                start = AckTheme.spacing.gutter,
                            ),
                        )
                        IconButton(
                            onClick = { controller.onViewStationDetails(viewState.callsign) },
                        ) {
                            Icon(Icons.Default.Info, stringResource(R.string.log_details_title))
                        }
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
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AckTheme.spacing.content)) {
                    Column(modifier = Modifier.padding(AckTheme.spacing.content)) {
                        Text("Debug Info", style = AckTheme.typography.h2)
                        Text("Raw Data", style = AckTheme.typography.h3)
                        Text(viewState.rawSource, style = AckTheme.typography.caption)
                        Spacer(Modifier.height(AckTheme.spacing.content))
                    }
                }
            }
        }
    }
}
