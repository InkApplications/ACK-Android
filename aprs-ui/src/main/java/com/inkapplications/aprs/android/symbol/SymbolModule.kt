package com.inkapplications.aprs.android.symbol

import dagger.Binds
import dagger.Module

@Module
abstract class SymbolModule {
    @Binds
    abstract fun symbolFactory(symbolFactory: AndroidSymbolFactory): SymbolFactory
}
