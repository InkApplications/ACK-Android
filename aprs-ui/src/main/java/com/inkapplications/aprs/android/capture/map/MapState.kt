package com.inkapplications.aprs.android.capture.map

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapState @Inject constructor() {
    val trackMyPosition = MutableStateFlow(false)
}
