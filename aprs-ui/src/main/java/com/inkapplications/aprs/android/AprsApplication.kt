package com.inkapplications.aprs.android

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.android.extensions.LifecycleLogger
import com.mapbox.mapboxsdk.Mapbox
import kimchi.Kimchi
import kimchi.KimchiLoggerAnalytics
import kimchi.analytics.bridge.firebase.FirebaseAnalyticsAdapter
import kimchi.logger.defaultWriter

class AprsApplication: Application() {
    val component by lazy {
        DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        Kimchi.addLog(defaultWriter)
        Kimchi.addAnalytics(KimchiLoggerAnalytics)
        Kimchi.addAnalytics(FirebaseAnalyticsAdapter())

        registerActivityLifecycleCallbacks(LifecycleLogger {
            Kimchi.trace(it)
        })

        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
    }
}

val Activity.component get() = (application as AprsApplication).component
val Fragment.component get() = (activity!!.application as AprsApplication).component
