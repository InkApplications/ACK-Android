package com.inkapplications.ack.android.capture

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.log.AprsLogItem
import com.inkapplications.ack.android.capture.log.LogItemViewModel
import com.inkapplications.ack.android.capture.map.MapScreen
import com.inkapplications.ack.android.capture.map.MapViewModel
import com.inkapplications.ack.android.trackNavigation
import com.inkapplications.ack.android.ui.theme.AprsScreen
import com.inkapplications.ack.android.ui.theme.AprsTheme
import com.inkapplications.android.extensions.control.ControlState
import com.inkapplications.android.extensions.control.whenDisabled
import com.inkapplications.android.extensions.control.whenEnabled
import kimchi.Kimchi

@Composable
fun CaptureScreen(
    captureScreenState: State<CaptureScreenViewModel>,
    mapState: State<MapViewModel>,
    logs: State<List<LogItemViewModel>>,
    mapFactory: (Context) -> View,
    controller: CaptureNavController,
) = AprsScreen {
    val navController = rememberNavController()

    Column {
        CaptureAppBar(
            recordingState = captureScreenState.value.recordingState,
            internetState = captureScreenState.value.internetServiceState,
            transmitState = captureScreenState.value.transmitState,
            controller = controller,
        )
        Box(
            modifier = Modifier.weight(1f),
        ) {
            NavHost(
                navController = navController,
                startDestination = "map",
                modifier = Modifier.fillMaxSize(),
            ) {
                composable("map") {
                    MapScreen(
                        state = mapState.value,
                        mapFactory = mapFactory,
                        onLogItemClick = controller::onLogItemClick,
                        onEnableLocation = controller::onLocationEnableClick,
                        onDisableLocation = controller::onLocationDisableClick,
                    )
                }
                composable("log") {
                    AprsScreen {
                        LazyColumn {
                            items(logs.value) { log ->
                                AprsLogItem(log, controller::onLogItemClick)
                            }
                        }
                    }
                }
            }
        }

        BottomNavigation(
            backgroundColor = AprsTheme.colors.surface,
            contentColor = contentColorFor(AprsTheme.colors.surface),
        ) {
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Map, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_map)) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == "map",
                onClick = {
                    Kimchi.info("Navigate to map")
                    Kimchi.trackNavigation("map")
                    navController.navigate("map")
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                label = { Text(stringResource(R.string.menu_capture_log)) },
                selected = navController.currentBackStackEntryAsState().value?.destination?.route == "log",
                onClick = {
                    Kimchi.info("Navigate to log")
                    Kimchi.trackNavigation("log")
                    navController.navigate("log")
                }
            )
        }
    }
}

@Composable
fun CaptureAppBar(
    recordingState: ControlState,
    internetState: ControlState,
    transmitState: ControlState,
    controller: CaptureNavController,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name_short))
        },
        backgroundColor = AprsTheme.colors.surface,
        contentColor = contentColorFor(AprsTheme.colors.surface),
        actions = {
            transmitState.whenEnabled {
                IconButton(
                    onClick = controller::onTransmitDisableClick,
                ) {
                    Icon(
                        Icons.Default.WifiTethering,
                        contentDescription = "Disable Transmission",
                        tint = AprsTheme.colors.brand,
                    )
                }
            }
            transmitState.whenDisabled {
                IconButton(
                    onClick = controller::onTransmitEnableClick,
                ) {
                    Icon(
                        Icons.Default.WifiTetheringOff,
                        contentDescription = "Enable Transmission",
                    )
                }
            }
            recordingState.whenEnabled {
                IconButton(
                    onClick = controller::onRecordingDisableClick,
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = stringResource(R.string.capture_record_disable),
                        tint = AprsTheme.colors.brand,
                    )
                }
            }
            recordingState.whenDisabled {
                IconButton(
                    onClick = controller::onRecordingEnableClick
                ) {
                    Icon(
                        Icons.Default.MicOff,
                        contentDescription = stringResource(R.string.capture_record_enable),
                    )
                }
            }
            internetState.whenEnabled {
                IconButton(
                    onClick = controller::onInternetServiceDisableClick,
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = "Disable APRS-IS",
                        tint = AprsTheme.colors.brand,
                    )
                }
            }
            internetState.whenDisabled {
                IconButton(
                    onClick = controller::onInternetServiceEnableClick,
                ) {
                    Icon(
                        Icons.Default.CloudOff,
                        contentDescription = "Enable APRS-IS"
                    )
                }
            }
            IconButton(
                onClick = controller::onSettingsClick
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_title)
                )
            }
        }
    )
}
