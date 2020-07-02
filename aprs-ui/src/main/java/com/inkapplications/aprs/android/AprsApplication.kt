package com.inkapplications.aprs.android

import android.app.Activity
import android.app.Application
import kimchi.Kimchi
import kimchi.logger.defaultWriter

class AprsApplication: Application() {
    val component by lazy {
        DaggerApplicationComponent.builder().build()
    }

    override fun onCreate() {
        super.onCreate()

        Kimchi.addLog(defaultWriter)
    }
}

val Activity.component get() = (application as AprsApplication).component
