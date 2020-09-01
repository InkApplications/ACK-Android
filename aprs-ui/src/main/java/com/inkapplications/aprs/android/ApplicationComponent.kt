package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.aprs.android.firebase.FirebaseModule
import com.inkapplications.aprs.android.capture.log.LogEvents
import com.inkapplications.aprs.android.capture.map.MapManagerFactory
import com.inkapplications.aprs.android.capture.map.MapModule
import com.inkapplications.aprs.android.settings.SettingsModule
import com.inkapplications.aprs.android.settings.SettingsAccess
import com.inkapplications.aprs.android.startup.ApplicationInitializer
import com.inkapplications.aprs.android.startup.StartupModule
import com.inkapplications.aprs.android.station.StationEvents
import com.inkapplications.aprs.data.AndroidAprsModule
import com.inkapplications.aprs.data.AprsAccess
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidAprsModule::class,
        ApplicationModule::class,
        ExternalModule::class,
        FirebaseModule::class,
        MapModule::class,
        SettingsModule::class,
        StartupModule::class
    ]
)
interface ApplicationComponent {
    fun aprs(): AprsAccess
    fun mapManager(): MapManagerFactory
    fun settingsRepository(): SettingsAccess
    fun logData(): LogEvents
    fun initializer(): ApplicationInitializer
    fun stationEvents(): StationEvents
}
