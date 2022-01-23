package com.inkapplications.ack.android

import kimchi.analytics.KimchiAnalytics
import kimchi.analytics.stringProperty

/**
 * Track navigation events within the app.
 */
fun KimchiAnalytics.trackNavigation(destination: String) = trackEvent(
    name = "navigate",
    properties = listOf(stringProperty("destination", destination))
)
