package com.inkapplications.aprs.data

object AndroidAprsModule {
    val aprsAccess: AprsAccess by lazy { AndroidAprs(AudioDataProcessor()) }
}
