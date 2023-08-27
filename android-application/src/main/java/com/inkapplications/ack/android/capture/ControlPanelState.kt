package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.android.extensions.control.ControlState

/**
 * Represents the states for the capture screen's control panel.
 */
sealed interface ControlPanelState {
    /**
     * State of the connection toggle button.
     */
    val connectState: ControlState

    /**
     * State of the position toggle button.
     */
    val positionTransmitState: ControlState

    /**
     * Object used before any data has been loaded successfully.
     */
    object Initial: ControlPanelState {
        override val connectState: ControlState = ControlState.Disabled
        override val positionTransmitState: ControlState = ControlState.Disabled
    }

    /**
     * Data for how to display the control panel based on application state.
     */
    data class Loaded(
        /**
         * The user's current callsign, as a readable string.
         */
        val userCallsign: String,

        /**
         * Level of the input volume, if applicable to the connection.
         */
        val volumeLevel: Float?,

        /**
         * Name of the current connection type.
         */
        val connection: String,

        /**
         * Data type for the current connection
         */
        val connectionType: DriverSelection,

        /**
         * State of the connection toggle button.
         */
        override val connectState: ControlState,

        /**
         * State of the position toggle button.
         */
        override val positionTransmitState: ControlState,
    ): ControlPanelState
}
