package com.inkapplications.ack.android.startup

import android.app.Application

interface ApplicationInitializer {
    suspend fun initialize(application: Application)
}
