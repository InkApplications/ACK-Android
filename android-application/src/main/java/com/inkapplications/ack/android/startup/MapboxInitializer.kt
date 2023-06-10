package com.inkapplications.ack.android.startup

import com.inkapplications.ack.android.BuildConfig
import com.mapbox.maps.ResourceOptionsManager

/**
 * Initialize Mapbox.
 */
val MapboxInitializer = ApplicationInitializer {
    ResourceOptionsManager.getDefault(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
}
