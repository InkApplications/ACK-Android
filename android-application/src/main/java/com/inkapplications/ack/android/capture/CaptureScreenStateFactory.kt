package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.connection.readableName
import com.inkapplications.ack.android.settings.LicenseData
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.control.ControlState
import dagger.Reusable
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.structure.toFloat
import javax.inject.Inject

/**
 * Create view state models from current state data.
 */
@Reusable
class CaptureScreenStateFactory @Inject constructor(
    private val stringResources: StringResources,
) {
    /**
     * Create a new model from the capture data provided.
     */
    fun controlPanelState(
        currentDriver: DriverSelection,
        driverConnectionState: DriverConnectionState,
        positionTransmit: Boolean,
        license: LicenseData,
        inputAudioLevel: Percentage?,
    ): ControlPanelState {
        return ControlPanelState.Loaded(
            userCallsign = license.address?.toString() ?: stringResources.getString(R.string.capture_callsign_missing),
            volumeLevel = inputAudioLevel?.toDecimal()?.toFloat(),
            connection = stringResources.getString(currentDriver.readableName),
            connectState = when {
                driverConnectionState == DriverConnectionState.Connected -> ControlState.On
                currentDriver == DriverSelection.Internet && license.address == null -> ControlState.Disabled
                else -> ControlState.Off
            },
            connectionType = currentDriver,
            positionTransmitState = when {
                positionTransmit -> ControlState.On
                driverConnectionState != DriverConnectionState.Connected -> ControlState.Disabled
                currentDriver == DriverSelection.Internet && license.passcode == null -> ControlState.Disabled
                license.address == null -> ControlState.Disabled
                else -> ControlState.Off
            },
        )
    }
}
