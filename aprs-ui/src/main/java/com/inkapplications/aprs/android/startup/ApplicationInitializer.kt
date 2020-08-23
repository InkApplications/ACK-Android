package com.inkapplications.aprs.android.startup

import android.app.Application

interface ApplicationInitializer {
    suspend fun initialize(application: Application)
}