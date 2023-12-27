package com.inkapplications.ack.android.capture.insights

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.log.AprsLogItem
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel(),
    controller: InsightsController,
) {
    AckScreen {
        val weatherState = viewModel.weatherState.collectAsState()
        val stationsState = viewModel.stations.collectAsState()
        val statsState = viewModel.statsState.collectAsState()

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).padding(
                top = AckTheme.spacing.gutter,
                start = AckTheme.spacing.gutter,
                end = AckTheme.spacing.gutter,
                bottom = AckTheme.spacing.navigationProtection + AckTheme.spacing.gutter,
            )
        ) {
            Text(stringResource(R.string.insights_title), style = AckTheme.typography.h1)
            Weather(weatherState.value)
            NearbyStations(stationsState.value, controller)
            Stats(statsState.value)
        }
    }
}

@Composable
private fun NearbyStations(
    state: NearbyStationsState,
    controller: InsightsController,
) {
    Column {
        Text(stringResource(R.string.insights_stations_title), style = AckTheme.typography.h2, modifier = Modifier.padding(vertical = AckTheme.spacing.content))
        when (state) {
            NearbyStationsState.Initial -> {}
            NearbyStationsState.Empty -> {
                Text(stringResource(id = R.string.insights_stations_empty_caption), style = AckTheme.typography.caption)
            }
            is NearbyStationsState.StationList -> {
                state.stations.forEach { station ->
                    AprsLogItem(station, onClick = controller::onStationItemClicked)
                }
            }
        }
    }
}

@Composable
private fun Stats(
    state: InsightsStatsState,
) {
    Column {
        Text(stringResource(R.string.insights_packets_title), style = AckTheme.typography.h2, modifier = Modifier.padding(vertical = AckTheme.spacing.content))
        when (state) {
            InsightsStatsState.Initial -> {}
            InsightsStatsState.None -> {
                Text(stringResource(id = R.string.insights_stats_placeholder), style = AckTheme.typography.body)
            }
            is InsightsStatsState.LoadedData -> {
                Text(stringResource(R.string.insights_packets_count, state.packets), style = AckTheme.typography.body)
                Text(stringResource(R.string.insights_stations_count, state.stations), style = AckTheme.typography.body)
            }
        }
    }
}

@Composable
private fun Weather(
    state: InsightsWeatherState,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AckTheme.spacing.content),
    ) {
        when (state) {
            InsightsWeatherState.Initial,
            InsightsWeatherState.Unknown -> WeatherPlaceholder()
            is InsightsWeatherState.DisplayRecent -> WeatherData(state)
        }
    }
}

@Composable
private fun WeatherData(
    data: InsightsWeatherState.DisplayRecent,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AckTheme.spacing.content),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherIcon(iconState = data.icon)
            Text(data.temperature, style = AckTheme.typography.display)
        }
        Text(
            text = stringResource(
                R.string.insights_weather_report_info_template,
                data.weatherReporter,
                data.weatherReportTime
            ),
            style = AckTheme.typography.caption
        )
    }
}

@Composable
private fun WeatherPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AckTheme.spacing.content),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Thermostat,
                contentDescription = stringResource(R.string.insights_weather_icon_normal),
                modifier = Modifier.size(AckTheme.sizing.dispayIcon),
                tint = AckTheme.colors.foreground,
            )
            Text("--", style = AckTheme.typography.display)
        }
        Text(
            text = stringResource(R.string.insights_weather_placeholder_caption),
            style = AckTheme.typography.caption
        )
    }
}

@Composable
private fun WeatherIcon(
    iconState: InsightsWeatherState.WeatherIcon,
) {
    val iconModifier = Modifier.size(AckTheme.sizing.displayDecorationIcon)
    val iconTint = AckTheme.colors.foreground

    when (iconState) {
        InsightsWeatherState.WeatherIcon.Normal -> Icon(
            imageVector = Icons.Default.Thermostat,
            contentDescription = stringResource(R.string.insights_weather_icon_normal),
            modifier = iconModifier,
            tint = iconTint,
        )
        InsightsWeatherState.WeatherIcon.Rain -> Icon(
            painter = painterResource(id = R.drawable.ic_rain),
            contentDescription = stringResource(R.string.insights_weather_icon_rain),
            modifier = iconModifier,
            tint = iconTint,
        )
        InsightsWeatherState.WeatherIcon.Snow -> Icon(
            painter = painterResource(id = R.drawable.ic_snow),
            contentDescription = stringResource(R.string.insights_weather_icon_snow),
            modifier = iconModifier,
            tint = iconTint,
        )
        InsightsWeatherState.WeatherIcon.Humid -> Icon(
            painter = painterResource(id = R.drawable.ic_humid),
            contentDescription = stringResource(R.string.insights_weather_icon_humid),
            modifier = iconModifier,
            tint = iconTint,
        )
        InsightsWeatherState.WeatherIcon.Windy -> Icon(
            painter = painterResource(id = R.drawable.ic_windy),
            contentDescription = stringResource(R.string.insights_weather_icon_wind),
            modifier = iconModifier,
            tint = iconTint,
        )
    }
}
