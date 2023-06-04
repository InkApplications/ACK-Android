package com.inkapplications.ack.android.settings.buildinfo

/**
 * View state for the build info composable
 */
sealed interface BuildInfoState {
    /**
     * Initial state before data has been loaded.
     */
    object Initial: BuildInfoState

    /**
     * Debug build info, limited useful information.
     */
    data class Debug(
        override val playServices: ServiceState,
    ): BuildInfoState, ServiceAvailabilityInfo

    /**
     * Release build info with full details.
     */
    data class Release(
        val versionStatment: String,
        override val playServices: ServiceState
    ): BuildInfoState, ServiceAvailabilityInfo

    /**
     * Availability descriptor for services on the device.
     */
    sealed interface ServiceAvailabilityInfo {
        /**
         * Availability of Google Play Services
         */
        val playServices: ServiceState
    }

    /**
     * Availability states for a service on the device.
     */
    enum class ServiceState {
        /**
         * Indicates that the service is available and usable.
         */
        Available,

        /**
         * Indicates that the device does not support this feature.
         */
        Unavailable,

        /**
         * Indicates that the feature was not configured or disabled at build time.
         */
        Unconfigured,
    }
}
