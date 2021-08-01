package com.inkapplications.aprs.android.station

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme
import com.inkapplications.aprs.android.ui.NavigationRow

@Composable
fun StationScreen(
    viewState: State<StationViewModel>,
    createMapView: (Context) -> View,
    onBackPressed: () -> Unit,
) = AprsScreen {
    Column {
        if (viewState.value.mapVisible) {
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
                Text(
                    viewState.value.name,
                    style = AprsTheme.Typography.h1,
                    modifier = Modifier.padding(
                        start = AprsTheme.Spacing.gutter,
                        top = AprsTheme.Spacing.gutter,
                        end = AprsTheme.Spacing.gutter,
                    ),
                )
            }
        } else {
            Box(
                Modifier.padding(
                    start = AprsTheme.Spacing.gutter,
                    top = AprsTheme.Spacing.gutter,
                    end = AprsTheme.Spacing.gutter,
                )
            ) {
                NavigationRow(viewState.value.name, onBackPressed)
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
            if (viewState.value.temperatureVisible) {
                InfoRow(painterResource(R.drawable.ic_weather), viewState.value.temperature)
            }
            if (viewState.value.windVisible) {
                InfoRow(painterResource(R.drawable.ic_wind), viewState.value.wind)
            }
            if (viewState.value.altitudeVisible) {
                InfoRow(painterResource(R.drawable.ic_altitude), viewState.value.altitude)
            }
            Text(viewState.value.comment)
        }
    }
}

@Composable
fun InfoRow(
    icon: Painter,
    text: String,
) = Row {
    Icon(icon, null, modifier = Modifier.padding(AprsTheme.Spacing.icon))
    Text(text)
}