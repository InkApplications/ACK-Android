package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.R
import com.inkapplications.ack.structures.station.StationAddress
import com.inkapplications.android.extensions.StringResources
import com.inkapplications.android.extensions.control.ControlState
import dagger.Reusable
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.toWholePercentage
import inkapplications.spondee.structure.format
import javax.inject.Inject
import kotlin.properties.Delegates

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
    fun controlPanelState(data: CaptureData): ControlPanelState {
        return ControlPanelState.Loaded(
            userCallsign = data.currentAddress?.toString()
                ?: stringResources.getString(R.string.capture_callsign_missing),
            audioCaptureState = if (data.audioCaptureEnabled) ControlState.On else ControlState.Off,
            internetCaptureState = when {
                data.currentAddress == null -> ControlState.Hidden
                data.internetCaptureEnabled -> ControlState.On
                else -> ControlState.Off
            },
            internetTransmitState = when {
                data.currentAddress == null || data.passcode == -1 -> ControlState.Hidden
                !data.internetCaptureEnabled -> ControlState.Disabled
                data.internetTransmitEnabled -> ControlState.On
                else -> ControlState.Off
            },
            audioTransmitState = when {
                data.currentAddress == null -> ControlState.Hidden
                !data.audioCaptureEnabled -> ControlState.Disabled
                data.audioTransmitEnabled -> ControlState.On
                else -> ControlState.Off
            },
            audioCaptureLevel = data.inputAudioLevel
                ?.toWholePercentage()
                ?.format()
                ?: stringResources.getString(R.string.capture_volume_off),
        )
    }

    /**
     * A temporary collection of arguments required to build state models.
     */
    class CaptureData {
        var audioCaptureEnabled by Delegates.notNull<Boolean>()
        var internetCaptureEnabled by Delegates.notNull<Boolean>()
        var audioTransmitEnabled by Delegates.notNull<Boolean>()
        var internetTransmitEnabled by Delegates.notNull<Boolean>()
        var currentAddress: StationAddress? = null
        var inputAudioLevel: Percentage? = null
        var passcode by Delegates.notNull<Int>()
    }
}
