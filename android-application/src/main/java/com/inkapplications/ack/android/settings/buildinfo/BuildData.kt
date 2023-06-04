package com.inkapplications.ack.android.settings.buildinfo

/**
 * Raw data about the current app build and its environment.
 */
data class BuildData(
    /**
     * Android build type, ie. debug/release.
     */
    val buildType: String,

    /**
     * Readable version name.
     */
    val versionName: String,

    /**
     * Canonical version code.
     */
    val versionCode: Int,

    /**
     * Git commit hash, if specified.
     */
    val commit: String?,

    /**
     * Whether or not the build was configured to use play services.
     */
    val usePlayServices: Boolean,

    /**
     * Whether or not play services is available on the device.
     */
    val playServicesAvailable: Boolean,
)
