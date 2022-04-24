package com.inkapplications.ack.android

import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.ack.android.capture.CaptureEvents
import com.inkapplications.ack.android.capture.insights.InsightEvents
import com.inkapplications.ack.android.firebase.FirebaseModule
import com.inkapplications.ack.android.capture.log.LogEvents
import com.inkapplications.ack.android.capture.log.LogModule
import com.inkapplications.ack.android.capture.map.MapEventsFactory
import com.inkapplications.ack.android.capture.map.MapModule
import com.inkapplications.ack.android.capture.messages.MessageEvents
import com.inkapplications.ack.android.capture.messages.MessagesModule
import com.inkapplications.ack.android.capture.service.CaptureServiceNotifications
import com.inkapplications.ack.android.locale.LocaleModule
import com.inkapplications.ack.android.onboard.OnboardingModule
import com.inkapplications.ack.android.onboard.OnboardingStateAccess
import com.inkapplications.ack.android.settings.SettingsModule
import com.inkapplications.ack.android.settings.SettingsAccess
import com.inkapplications.ack.android.startup.ApplicationInitializer
import com.inkapplications.ack.android.startup.StartupModule
import com.inkapplications.ack.android.station.StationEvents
import com.inkapplications.ack.android.station.StationModule
import com.inkapplications.ack.android.symbol.SymbolModule
import com.inkapplications.ack.android.connection.ConnectionModule
import com.inkapplications.ack.android.settings.license.LicensePromptValidator
import com.inkapplications.ack.android.transmit.TransmitModule
import com.inkapplications.ack.data.AndroidAprsModule
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
        MessagesModule::class,
        OnboardingModule::class,
        SettingsModule::class,
        StartupModule::class,
        StationModule::class,
        SymbolModule::class,
        TransmitModule::class,
        ConnectionModule::class,
    ]
)
interface ApplicationComponent {
    fun insightEvents(): InsightEvents
    fun captureEvents(): CaptureEvents
    fun mapManager(): MapEventsFactory
    fun settingsAccess(): SettingsAccess
    fun logData(): LogEvents
    fun messageEvents(): MessageEvents
    fun initializer(): ApplicationInitializer
    fun stationEvents(): StationEvents
    fun onboardingStateAccess(): OnboardingStateAccess
    fun licenseValidator(): LicensePromptValidator
    fun captureServiceNotifications(): CaptureServiceNotifications
}
