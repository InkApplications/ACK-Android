package com.inkapplications.android.extensions

import android.app.Application

interface ApplicationInitializer {
    fun onCreate(application: Application)
}