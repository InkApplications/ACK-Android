package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.aprs.android.capture.CaptureEvents
import com.inkapplications.aprs.android.firebase.FirebaseModule
import com.inkapplications.aprs.android.capture.log.LogEvents
import com.inkapplications.aprs.android.capture.log.LogModule
import com.inkapplications.aprs.android.capture.map.MapEventsFactory
import com.inkapplications.aprs.android.capture.map.MapModule
import com.inkapplications.aprs.android.locale.LocaleModule
import com.inkapplications.aprs.android.settings.SettingsModule
import com.inkapplications.aprs.android.settings.SettingsAccess
import com.inkapplications.aprs.android.startup.ApplicationInitializer
import com.inkapplications.aprs.android.startup.StartupModule
import com.inkapplications.aprs.android.station.StationEvents
import com.inkapplications.aprs.android.station.StationModule
import com.inkapplications.aprs.android.symbol.SymbolModule
import com.inkapplications.aprs.data.AndroidAprsModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidAprsModule::class,
        ApplicationModule::class,
        ExternalModule::class,
        FirebaseModule::class,
        LocaleModule::class,
        LogModule::class,
        MapModule::class,
        SettingsModule::class,
        StartupModule::class,
        StationModule::class,
        SymbolModule::class,
    ]
)
interface ApplicationComponent {
    fun captureEvents(): CaptureEvents
    fun mapManager(): MapEventsFactory
    fun settingsRepository(): SettingsAccess
    fun logData(): LogEvents
    fun initializer(): ApplicationInitializer
    fun stationEvents(): StationEvents
}
