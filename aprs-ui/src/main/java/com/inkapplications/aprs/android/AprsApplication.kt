package com.inkapplications.aprs.android

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.android.extensions.LifecycleLogger
import com.mapbox.mapboxsdk.Mapbox
import kimchi.Kimchi
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
        registerActivityLifecycleCallbacks(LifecycleLogger {
            Kimchi.trace(it)
        })

        Mapbox.getInstance(this, BuildConfig.MAPBOX_ACCESS_TOKEN)
        component.initializers().forEach { it.onCreate(this) }
    }
}

val Activity.component get() = (application as AprsApplication).component
val Fragment.component get() = (activity!!.application as AprsApplication).component
