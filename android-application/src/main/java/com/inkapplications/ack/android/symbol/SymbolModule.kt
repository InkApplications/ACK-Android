package com.inkapplications.ack.android.symbol

import dagger.Binds
import dagger.Module

@Module
abstract class SymbolModule {
    @Binds
    abstract fun symbolFactory(symbolFactory: AndroidSymbolFactory): SymbolFactory
}
