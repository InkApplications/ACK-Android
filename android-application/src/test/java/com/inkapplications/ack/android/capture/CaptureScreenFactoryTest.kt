package com.inkapplications.ack.android.capture

import com.inkapplications.ack.android.ParrotStringResources
import com.inkapplications.ack.android.connection.DriverSelection
import com.inkapplications.ack.android.settings.LicenseData
import com.inkapplications.ack.android.settings.Passcode
import com.inkapplications.ack.data.drivers.DriverConnectionState
import com.inkapplications.ack.structures.station.toStationAddress
import com.inkapplications.android.extensions.bluetooth.BluetoothDeviceData
import com.inkapplications.android.extensions.control.ControlState
import inkapplications.spondee.scalar.percent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CaptureScreenFactoryTest {
    private val factory = CaptureScreenStateFactory(
        stringResources = ParrotStringResources,
    )

    private val FakeDevice = BluetoothDeviceData("", null, "", null, null, 0)

    @Test
    fun internetDefault() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Internet,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("", result.userCallsign)
        assertEquals(null, result.volumeLevel)
        assertEquals("", result.connection)
        assertEquals(DriverSelection.Internet, result.connectionType)
        assertEquals(ControlState.Disabled, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
    }

    @Test
    fun internetWithCallsign() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Internet,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress()
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("KE0YOG-7", result.userCallsign)
        assertEquals(ControlState.Off, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
    }

    @Test
    fun internetWithPasscode() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Internet,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
                passcode = Passcode(12345),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("KE0YOG-7", result.userCallsign)
        assertEquals(ControlState.Off, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
    }

    @Test
    fun internetConnectedWithPasscode() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Internet,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = false,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
                passcode = Passcode(12345),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("KE0YOG-7", result.userCallsign)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.Off, result.positionTransmitState)
    }

    @Test
    fun internetConnectedWithTransmit() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Internet,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = true,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
                passcode = Passcode(12345),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals("KE0YOG-7", result.userCallsign)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.On, result.positionTransmitState)
    }

    @Test
    fun audioDefault() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Audio,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.Off, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
        assertEquals(null, result.volumeLevel)
    }

    @Test
    fun audioCapturing() {
        val result = factory.controlPanelState(
            inputAudioLevel = 12.percent,
            currentDriver = DriverSelection.Audio,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = false,
            license = LicenseData(),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
        assertEquals(.12f, result.volumeLevel)
    }

    @Test
    fun audioRegistered() {
        val result = factory.controlPanelState(
            inputAudioLevel = 12.percent,
            currentDriver = DriverSelection.Audio,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.Off, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
        assertEquals(.12f, result.volumeLevel)
    }

    @Test
    fun audioRegisteredConnected() {
        val result = factory.controlPanelState(
            inputAudioLevel = 12.percent,
            currentDriver = DriverSelection.Audio,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = false,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.Off, result.positionTransmitState)
        assertEquals(.12f, result.volumeLevel)
    }

    @Test
    fun audioTransmitting() {
        val result = factory.controlPanelState(
            inputAudioLevel = 12.percent,
            currentDriver = DriverSelection.Audio,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = true,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.On, result.positionTransmitState)
        assertEquals(.12f, result.volumeLevel)
    }

    @Test
    fun tncDefault() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Tnc,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.Off, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
        assertEquals(null, result.volumeLevel)
    }

    @Test
    fun tncConnectedOff() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Tnc,
            driverConnectionState = DriverConnectionState.Disconnected,
            positionTransmit = false,
            license = LicenseData(),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.Off, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
        assertEquals(null, result.volumeLevel)
    }

    @Test
    fun tncConnectedEnabled() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Tnc,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = false,
            license = LicenseData(),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.Disabled, result.positionTransmitState)
        assertEquals(null, result.volumeLevel)
    }

    @Test
    fun tncConnectedEnabledRegistered() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Tnc,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = false,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.Off, result.positionTransmitState)
        assertEquals(null, result.volumeLevel)
    }

    @Test
    fun tncTransmitting() {
        val result = factory.controlPanelState(
            inputAudioLevel = null,
            currentDriver = DriverSelection.Tnc,
            driverConnectionState = DriverConnectionState.Connected,
            positionTransmit = true,
            license = LicenseData(
                address = "KE0YOG-7".toStationAddress(),
            ),
        )

        assertTrue(result is ControlPanelState.Loaded)
        assertEquals(ControlState.On, result.connectState)
        assertEquals(ControlState.On, result.positionTransmitState)
        assertEquals(null, result.volumeLevel)
    }
}
