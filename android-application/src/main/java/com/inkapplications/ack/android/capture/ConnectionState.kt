package com.inkapplications.ack.android.capture

import com.inkapplications.ack.data.drivers.ConnectTncData

/**
 * Represents the possible states of an APRS connection.
 */
enum class ConnectionState {
    Disconnected,
    Connecting,
    Connected,
}

/**
 * Infer the connection state representation through data provided by the TNC driver.
 */
fun ConnectTncData.toConnectionState() = when {
    connectedDevice != null -> ConnectionState.Connected
    connectingDevice != null -> ConnectionState.Connecting
    else -> ConnectionState.Disconnected
}
