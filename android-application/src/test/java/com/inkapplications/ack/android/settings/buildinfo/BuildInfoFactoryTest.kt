package com.inkapplications.ack.android.settings.buildinfo

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.android.extensions.StringResources
import kimchi.logger.EmptyLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildInfoFactoryTest {
    private val factory = BuildInfoFactory(
        stringResources = ParrotStringResources,
        logger = EmptyLogger,
    )
    private val data = BuildData(
        buildType = "debug",
        versionName = "1.0.0",
        versionCode = 1,
        commit = "ffac537e6cbbf934b08745a378932722df287a53",
        usePlayServices = true,
        playServicesAvailable = true,
    )

    @Test
    fun release() {
        val result = factory.buildInfo(data.copy(buildType = "release"))

        assertTrue(result is BuildInfoState.Release)
        assertEquals(result.playServices, BuildInfoState.ServiceState.Available)
        assertEquals("1.0.0|1|ffac537", result.versionStatment)
    }

    @Test
    fun shortHash() {
        val result = factory.buildInfo(data.copy(buildType = "release", commit = "ff"))

        assertTrue(result is BuildInfoState.Release)
        assertEquals("1.0.0|1|ff", result.versionStatment)
    }

    @Test
    fun noHash() {
        val factory = BuildInfoFactory(
            stringResources = object: StringResources by ParrotStringResources {
                override fun getString(key: Int): String = "Test"
            },
            logger = EmptyLogger,
        )
        val result = factory.buildInfo(data.copy(buildType = "release", commit = null))

        assertTrue(result is BuildInfoState.Release)
        assertEquals("1.0.0|1|Test", result.versionStatment)
    }

    @Test
    fun debugWithPlayServices() {
        val result = factory.buildInfo(data)

        assertTrue(result is BuildInfoState.Debug)
        assertEquals(result.playServices, BuildInfoState.ServiceState.Available)
    }

    @Test
    fun playServicesMissing() {
        val result = factory.buildInfo(data.copy(playServicesAvailable = false))

        assertTrue(result is BuildInfoState.Debug)
        assertEquals(result.playServices, BuildInfoState.ServiceState.Unavailable)
    }

    @Test
    fun playServicesUnconfigured() {
        val result = factory.buildInfo(data.copy(usePlayServices = false))

        assertTrue(result is BuildInfoState.Debug)
        assertEquals(result.playServices, BuildInfoState.ServiceState.Unconfigured)
    }
}
