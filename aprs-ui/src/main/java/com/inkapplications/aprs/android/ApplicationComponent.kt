package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.aprs.android.log.LogDataAccess
import com.inkapplications.aprs.android.map.MapDataRepository
import com.inkapplications.aprs.data.AndroidAprsModule
import com.inkapplications.aprs.data.AprsAccess
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidAprsModule::class,
        ApplicationModule::class,
        ExternalModule::class
    ]
)
interface ApplicationComponent {
    fun aprs(): AprsAccess
    fun mapData(): MapDataRepository
    fun logData(): LogDataAccess
}
