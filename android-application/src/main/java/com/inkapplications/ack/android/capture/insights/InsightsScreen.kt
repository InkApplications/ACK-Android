package com.inkapplications.ack.android.capture.insights

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun InsightsScreen(
    state: InsightsViewState,
) {
    AckScreen {
        when (state) {
            is InsightsViewState.Loaded -> Insights(state)
            InsightsViewState.Empty -> EmptyPlaceholder()
            InsightsViewState.Initial -> {}
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = AckTheme.dimensions.navigationProtection).fillMaxSize()
    ) {
        Icon(
            imageVector = Icons.Default.Lightbulb,
            contentDescription = null,
            tint = AckTheme.colors.foregroundInactive,
            modifier = Modifier.size(AckTheme.dimensions.placeholderIcon),
        )
        Text(stringResource(R.string.insights_empty))
    }
}

@Composable
private fun Insights(
    state: InsightsViewState.Loaded,
) {
    Column(
        modifier = Modifier.padding(AckTheme.dimensions.gutter)
    ) {
        Text(stringResource(R.string.insights_title), style = AckTheme.typography.h1)
        if (state.weatherVisible) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(vertical = AckTheme.dimensions.content),
            ) {
                Text(state.temperature, style = AckTheme.typography.display)
                Text(stringResource(R.string.insights_weather_report_info_template, state.weatherReporter, state.weatherReportTime), style = AckTheme.typography.caption)
            }
        }
        Text(stringResource(R.string.insights_packets_title), style = AckTheme.typography.h2, modifier = Modifier.padding(bottom = AckTheme.dimensions.content))
        Text(stringResource(R.string.insights_packets_count, state.packets), style = AckTheme.typography.body)
        Text(stringResource(R.string.insights_stations_count, state.stations), style = AckTheme.typography.body)
    }
}
