package com.inkapplications.ack.android.capture

import com.inkapplications.android.extensions.control.ControlState

/**
 * Represents the states for the capture screen's control panel.
 */
sealed interface ControlPanelState {
    /**
     * View state of the audio capture toggle button.
     */
    val audioCaptureState: ControlState

    /**
     * View state of the internet capture toggle button.
     */
    val internetCaptureState: ControlState

    /**
     * View state of the internet transmission toggle button.
     */
    val internetTransmitState: ControlState

    /**
     * View state of the audio transmission toggle button.
     */
    val audioTransmitState: ControlState

    /**
     * Object used before any data has been loaded successfully.
     */
    object Initial: ControlPanelState {
        override val audioCaptureState: ControlState = ControlState.Hidden
        override val internetCaptureState: ControlState = ControlState.Hidden
        override val internetTransmitState: ControlState = ControlState.Hidden
        override val audioTransmitState: ControlState = ControlState.Hidden
    }

    /**
     * Data for how to display the control panel based on application state.
     */
    data class Loaded(
        /**
         * The user's current callsign, as a readable string.
         */
        val userCallsign: String,
        override val audioCaptureState: ControlState,
        override val internetCaptureState: ControlState,
        override val internetTransmitState: ControlState,
        override val audioTransmitState: ControlState,

        /**
         * A readable string for the user's current audio level when
         * capturing audio.
         */
        val audioCaptureLevel: String,
    ): ControlPanelState
}
