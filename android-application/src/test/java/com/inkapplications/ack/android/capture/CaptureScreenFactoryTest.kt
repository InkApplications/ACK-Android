package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.ack.android.settings.Passcode
import com.inkapplications.ack.structures.station.toStationAddress
import com.inkapplications.android.extensions.control.ControlState
import inkapplications.spondee.scalar.percent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptureScreenFactoryTest {
    private val data = CaptureScreenStateFactory.CaptureData().apply {
        audioCaptureEnabled = false
        internetCaptureEnabled = false
        audioTransmitEnabled = false
        internetTransmitEnabled = false
        currentAddress = null
        inputAudioLevel = null
        passcode = null
    }
    private val factory = CaptureScreenStateFactory(
        stringResources = ParrotStringResources,
    )

    @Test
    fun default() {
        val result = factory.controlPanelState(data)

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("", result.userCallsign)
        assertEquals(ControlState.Off, result.audioCaptureState)
        assertEquals(ControlState.Hidden, result.internetCaptureState)
        assertEquals(ControlState.Hidden, result.audioTransmitState)
        assertEquals(ControlState.Hidden, result.internetTransmitState)
        assertEquals("", result.audioCaptureLevel)
    }

    @Test
    fun fullCallsignRendered() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
        })

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("KE0YOG-7", result.userCallsign)
    }

    @Test
    fun audioCapturing() {
        val result = factory.controlPanelState(data.apply {
            audioCaptureEnabled = true
        })

        assertEquals(ControlState.On, result.audioCaptureState)
        assertEquals(ControlState.Hidden, result.audioTransmitState)
        assertEquals(ControlState.Hidden, result.internetCaptureState)
        assertEquals(ControlState.Hidden, result.internetTransmitState)
    }

    @Test
    fun callsignControlsAvailable() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
        })

        assertEquals(ControlState.Off, result.audioCaptureState)
        assertEquals(ControlState.Disabled, result.audioTransmitState)
        assertEquals(ControlState.Off, result.internetCaptureState)
        assertEquals(ControlState.Hidden, result.internetTransmitState)
    }

    @Test
    fun audioTransmitEnabled() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
            audioCaptureEnabled = true
        })

        assertEquals(ControlState.On, result.audioCaptureState)
        assertEquals(ControlState.Off, result.audioTransmitState)
        assertEquals(ControlState.Off, result.internetCaptureState)
        assertEquals(ControlState.Hidden, result.internetTransmitState)
    }

    @Test
    fun audioTransmitting() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
            audioCaptureEnabled = true
            audioTransmitEnabled = true
        })

        assertEquals(ControlState.On, result.audioCaptureState)
        assertEquals(ControlState.On, result.audioTransmitState)
        assertEquals(ControlState.Off, result.internetCaptureState)
        assertEquals(ControlState.Hidden, result.internetTransmitState)
    }

    @Test
    fun internetCapturing() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
            internetCaptureEnabled = true
        })

        assertEquals(ControlState.Off, result.audioCaptureState)
        assertEquals(ControlState.Disabled, result.audioTransmitState)
        assertEquals(ControlState.On, result.internetCaptureState)
        assertEquals(ControlState.Hidden, result.internetTransmitState)
    }

    @Test
    fun passcodeAvailable() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
            passcode = Passcode(123)
        })

        assertEquals(ControlState.Off, result.audioCaptureState)
        assertEquals(ControlState.Disabled, result.audioTransmitState)
        assertEquals(ControlState.Off, result.internetCaptureState)
        assertEquals(ControlState.Disabled, result.internetTransmitState)
    }

    @Test
    fun internetTransmitEnabled() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
            passcode = Passcode(123)
            internetCaptureEnabled = true
        })

        assertEquals(ControlState.Off, result.audioCaptureState)
        assertEquals(ControlState.Disabled, result.audioTransmitState)
        assertEquals(ControlState.On, result.internetCaptureState)
        assertEquals(ControlState.Off, result.internetTransmitState)
    }

    @Test
    fun internetTransmitting() {
        val result = factory.controlPanelState(data.apply {
            currentAddress = "KE0YOG-7".toStationAddress()
            passcode = Passcode(123)
            internetCaptureEnabled = true
            internetTransmitEnabled = true
        })

        assertEquals(ControlState.Off, result.audioCaptureState)
        assertEquals(ControlState.Disabled, result.audioTransmitState)
        assertEquals(ControlState.On, result.internetCaptureState)
        assertEquals(ControlState.On, result.internetTransmitState)
    }

    @Test
    fun audioLevel() {
        val result = factory.controlPanelState(data.apply {
            inputAudioLevel = 4.percent
        })

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("4%", result.audioCaptureLevel)
    }
}
