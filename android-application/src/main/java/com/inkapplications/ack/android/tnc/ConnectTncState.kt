package com.inkapplications.ack.android.tnc

/**
 * View data for the TNC connection screen.
 */
sealed interface ConnectTncState {
    /**
     * Initial state of the screen, before any data is loaded.
     */
    object Initial: ConnectTncState

    /**
     * State indicating that bluetooth discovery is running.
     */
    sealed interface Discovering: ConnectTncState {
        /**
         * Bluetooth discovery is running, but no compatible devices have been found.
         */
        object Empty: Discovering

        /**
         * A list of compatible TNC devices and their states.
         */
        data class DeviceList(val devices: List<DeviceItem>): Discovering
    }
}
