package com.inkapplications.ack.android

import android.app.Application
import com.inkapplications.ack.android.startup.InitJob
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AprsApplication: Application() {
    @Inject
    lateinit var initJob: InitJob

    override fun onCreate() {
        super.onCreate()

        initJob.init(this)
    }
}
