package com.inkapplications.ack.android.capture.insights

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
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
    if (state !is InsightsViewState.InsightsViewModel) return
    AckScreen {
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
}
