package com.inkapplications.aprs.android

import android.app.Activity
import android.app.Application

class AprsApplication: Application() {
    val component by lazy {
        DaggerApplicationComponent.builder().build()
    }
}

val Activity.component get() = (application as AprsApplication).component
