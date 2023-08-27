package com.inkapplications.ack.android.tnc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncDisabled
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.inkapplications.ack.android.ui.NavigationRow
import com.inkapplications.ack.android.ui.theme.AckScreen
import com.inkapplications.ack.android.ui.theme.AckTheme

@Composable
fun ConnectTncScreen(
    controller: DeviceListController,
) = AckScreen {
    DeviceList(controller = controller)
}

@Composable
fun DeviceList(
    viewModel: ConnectTncViewModel = hiltViewModel(),
    controller: DeviceListController,
) {
    val state = viewModel.state.collectAsState()
    when (val stateValue = state.value) {
        ConnectTncState.Initial,
        is ConnectTncState.Discovering.Empty -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                DeviceHeader(controller::onCloseClick)
                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        horizontal = AckTheme.spacing.gutter,
                    ),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.BluetoothSearching,
                        contentDescription = null,
                        tint = AckTheme.colors.foregroundInactive,
                        modifier = Modifier.size(AckTheme.sizing.dispayIcon),
                    )
                    Text("Searching for devices...")
                    LinearProgressIndicator(
                        modifier = Modifier.padding(vertical = AckTheme.spacing.item),
                    )
                }
            }
        }
        is ConnectTncState.Discovering.DeviceList -> {
            LazyColumn(
                contentPadding = PaddingValues(
                    bottom = AckTheme.spacing.gutter,
                ),
            ) {
                val rows = listOf<RowType>(RowType.Header) + stateValue.devices.map { RowType.Device(it) }
                items(rows) { row ->
                    when (row) {
                        RowType.Header -> DeviceHeader(controller::onCloseClick)
                        is RowType.Device -> Device(
                            device = row.device,
                            onConnectClick = { controller.onDeviceConnectClick(row.device) },
                            onDisconnectClick = controller::onDisconnect,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceHeader(
    onBackPress: () -> Unit,
) {
    NavigationRow(
        title = "Devices",
        onBackPressed = onBackPress,
    )
}

private sealed interface RowType {
    object Header: RowType
    data class Device(val device: DeviceItem): RowType
}

@Composable
private fun Device(
    device: DeviceItem,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = AckTheme.spacing.gutter,
                vertical = AckTheme.spacing.singleItem,
            ),
    ) {
        Box(
            modifier = Modifier.padding(AckTheme.spacing.item),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column {
                    Text(
                        text = device.name,
                        style = AckTheme.typography.h3,
                    )
                }
                DeviceConnectionOptions(
                    device = device,
                    onConnectClick = onConnectClick,
                    onDisconnectClick = onDisconnectClick,
                    onReconnectClick = {},
                )
            }
        }
    }
}

@Composable
private fun DeviceConnectionOptions(
    device: DeviceItem,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onReconnectClick: () -> Unit,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
) {
    when (device) {
        is DeviceItem.Connected -> {
            // TODO: Reconnection implementation
//            if (device.reconnect) {
//                IconButton(onClick = onReconnectClick) {
//                    Icon(
//                        imageVector = Icons.Default.SyncDisabled,
//                        contentDescription = "",
//                    )
//                }
//            } else {
//                IconButton(onClick = onReconnectClick) {
//                    Icon(
//                        imageVector = Icons.Default.Sync,
//                        contentDescription = "",
//                    )
//                }
//            }
            IconButton(onClick = onDisconnectClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Disconnect",
                )
            }
        }
        is DeviceItem.Connecting -> {
            Box(Modifier.padding(AckTheme.spacing.clickSafety)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AckTheme.sizing.iconButton),
                )
            }
        }
        is DeviceItem.NotConnected -> {
            // TODO: Reconnection implementation
//            if (device.reconnect) {
//                IconButton(onClick = onReconnectClick) {
//                    Icon(
//                        imageVector = Icons.Default.SyncDisabled,
//                        contentDescription = "",
//                    )
//                }
//            } else {
//                IconButton(onClick = onReconnectClick) {
//                    Icon(
//                        imageVector = Icons.Default.Sync,
//                        contentDescription = "",
//                    )
//                }
//            }
            IconButton(onClick = onConnectClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Connect",
                )
            }
        }
        is DeviceItem.Unpaired -> {
            IconButton(onClick = onConnectClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Connect",
                )
            }
        }
    }
}
