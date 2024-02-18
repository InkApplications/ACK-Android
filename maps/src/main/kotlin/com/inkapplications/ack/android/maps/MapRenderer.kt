package com.inkapplications.ack.android.maps

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.inkapplications.ack.data.CaptureId

interface MapRenderer {
    @Composable
    fun renderMarkerMap(
        viewModel: MapViewModel,
        onMapItemClicked: (CaptureId?) -> Unit,
        bottomProtection: Dp,
        interactive: Boolean,
        modifier: Modifier,
    )

    suspend fun initialize(application: Application)
}
