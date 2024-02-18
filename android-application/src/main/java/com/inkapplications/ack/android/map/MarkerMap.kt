package com.inkapplications.ack.android.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.inkapplications.ack.android.maps.MapViewModel
import com.inkapplications.ack.android.maps.MapsImplementation
import com.inkapplications.ack.data.CaptureId


@Composable
fun MarkerMap(
    viewModel: MapViewModel,
    onMapItemClicked: (CaptureId?) -> Unit,
    bottomProtection: Dp = 0.dp,
    interactive: Boolean = true,
    modifier: Modifier,
) = MapsImplementation.renderMarkerMap(
    viewModel = viewModel,
    onMapItemClicked = onMapItemClicked,
    bottomProtection = 0.dp,
    interactive = interactive,
    modifier = modifier,
)
