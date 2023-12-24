package com.inkapplications.ack.android.tnc

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
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
@OptIn(ExperimentalMaterialApi::class)
private fun Device(
    device: DeviceItem,
    onConnectClick: () -> Unit,
) {
    Card(
        onClick = onConnectClick,
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
                )
            }
        }
    }
}

@Composable
private fun DeviceConnectionOptions(
    device: DeviceItem,
) = Row(
    verticalAlignment = Alignment.CenterVertically,
) {
    when (device) {
        is DeviceItem.Connecting -> {
            Box(Modifier.padding(AckTheme.spacing.clickSafety)) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AckTheme.sizing.iconButton),
                )
            }
        }
        else -> {
            Box(Modifier.padding(AckTheme.spacing.clickSafety)) {
                Spacer(modifier = Modifier.size(AckTheme.sizing.iconButton))
            }
        }
    }
}
