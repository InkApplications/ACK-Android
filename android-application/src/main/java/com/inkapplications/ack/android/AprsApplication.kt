package com.inkapplications.ack.android

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.fragment.app.Fragment
import com.inkapplications.ack.android.capture.service.CaptureServiceNotifications
import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.android.extensions.LifecycleLogger
import com.mapbox.maps.ResourceOptionsManager
import dagger.hilt.android.HiltAndroidApp
import kimchi.bridge.firebase.analytics.FirebaseAnalyticsAdapter
import kimchi.Kimchi
import kimchi.KimchiLoggerAnalytics
import kimchi.bridge.firebase.crashlytics.FirebaseCrashlyticsExceptionAdapter
import kimchi.bridge.firebase.crashlytics.FirebaseCrashlyticsLogMessageAdapter
import kimchi.logger.LogLevel
import kimchi.logger.defaultWriter
import kimchi.logger.withThreshold
import javax.inject.Inject

@HiltAndroidApp
class AprsApplication: Application() {
    @Inject
    lateinit var captureServiceNotifications: CaptureServiceNotifications

    override fun onCreate() {
        super.onCreate()

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

        ResourceOptionsManager.getDefault(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        captureServiceNotifications.onCreate(this)
    }
}

