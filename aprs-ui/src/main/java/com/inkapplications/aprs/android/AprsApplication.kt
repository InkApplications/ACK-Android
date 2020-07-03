package com.inkapplications.aprs.android

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.inkapplications.android.extensions.LifecycleLogger
import kimchi.Kimchi
import kimchi.logger.defaultWriter

class AprsApplication: Application() {
    val component by lazy {
        DaggerApplicationComponent.builder().build()
    }

    override fun onCreate() {
        super.onCreate()

        Kimchi.addLog(defaultWriter)
        registerActivityLifecycleCallbacks(LifecycleLogger {
            Kimchi.trace(it)
        })
    }
}

val Activity.component get() = (application as AprsApplication).component
val Fragment.component get() = (activity!!.application as AprsApplication).component
