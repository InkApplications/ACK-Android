package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.ApplicationModule
import com.inkapplications.aprs.android.symbol.SymbolFactory
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
    fun symbolFactory(): SymbolFactory
}
