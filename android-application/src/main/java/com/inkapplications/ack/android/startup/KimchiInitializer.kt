package com.inkapplications.ack.android.startup

import com.inkapplications.ack.android.BuildConfig
import com.inkapplications.android.extensions.LifecycleLogger
import kimchi.Kimchi
import kimchi.KimchiLoggerAnalytics
import kimchi.bridge.firebase.analytics.FirebaseAnalyticsAdapter
import kimchi.bridge.firebase.crashlytics.FirebaseCrashlyticsExceptionAdapter
import kimchi.bridge.firebase.crashlytics.FirebaseCrashlyticsLogMessageAdapter
import kimchi.logger.LogLevel
import kimchi.logger.defaultWriter
import kimchi.logger.withThreshold

/**
 * Initializes the Kimchi Logging Framework.
 */
val KimchiInitializer = ApplicationInitializer(ApplicationInitializer.Priority.HIGH) {
    Kimchi.addLog(defaultWriter)
    if (BuildConfig.USE_GOOGLE_SERVICES) {
        Kimchi.addLog(FirebaseCrashlyticsLogMessageAdapter().withThreshold(LogLevel.INFO))
        Kimchi.addLog(FirebaseCrashlyticsExceptionAdapter())
        Kimchi.addAnalytics(FirebaseAnalyticsAdapter())
    }
    Kimchi.addAnalytics(KimchiLoggerAnalytics)

    registerActivityLifecycleCallbacks(LifecycleLogger {
        Kimchi.trace(it)
    })
}
