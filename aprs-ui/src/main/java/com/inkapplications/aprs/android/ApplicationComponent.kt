package com.inkapplications.aprs.android

import com.inkapplications.aprs.data.AndroidAprsModule
import com.inkapplications.aprs.data.AprsAccess
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidAprsModule::class
    ]
)
interface ApplicationComponent {
    fun aprs(): AprsAccess
}
