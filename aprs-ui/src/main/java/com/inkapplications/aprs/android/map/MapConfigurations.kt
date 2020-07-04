package com.inkapplications.aprs.android.map

import android.app.Activity
import android.content.res.Configuration
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.MapStyleOptions
import com.inkapplications.aprs.android.R

/**
 * Google map initializations run when the map is first created.
 */
fun GoogleMap.configure(activity: Activity) {
    if (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
        setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_dark))
    } else {
        setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_light))
    }
}
