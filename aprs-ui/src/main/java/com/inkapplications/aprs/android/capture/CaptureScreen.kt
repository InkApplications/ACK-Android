package com.inkapplications.aprs.android.capture

import android.content.Context
import android.view.View
import androidx.compose.foundation.layout.Column
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
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.capture.log.AprsLogItem
import com.inkapplications.aprs.android.capture.log.LogItemState
import com.inkapplications.aprs.android.capture.map.MapScreen
import com.inkapplications.aprs.android.capture.map.MapViewModel
import com.inkapplications.aprs.android.trackNavigation
import com.inkapplications.aprs.android.ui.AprsScreen
import com.inkapplications.aprs.android.ui.AprsTheme
import kimchi.Kimchi

@Composable
fun CaptureScreen(
    captureScreenState: State<CaptureScreenState>,
    mapState: State<MapViewModel>,
    logs: State<List<LogItemState>>,
    mapFactory: (Context) -> View,
    onRecordingEnableClick: () -> Unit,
    onRecordingDisableClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogClick: (LogItemState) -> Unit,
    onLocationEnableClick: () -> Unit,
    onLocationDisableClick: () -> Unit,
    onInternetServiceDisableClick: () -> Unit,
    onInternetServiceEnableClick: () -> Unit,
) = AprsScreen {
    val navController = rememberNavController()

    Column {
        CaptureAppBar(
            recordingEnabled = captureScreenState.value.recordingEnabled,
            onRecordingEnableClick = onRecordingEnableClick,
            onRecordingDisableClick = onRecordingDisableClick,
            onSettingsClick = onSettingsClick,
            internetServiceEnabled = captureScreenState.value.internetServiceEnabled,
            onInternetServiceDisableClick = onInternetServiceDisableClick,
            onInternetServiceEnableClick = onInternetServiceEnableClick,
        )

        NavHost(
            navController = navController,
            startDestination = "map",
            modifier = Modifier.weight(1f),
        ) {
            composable("map") {
                MapScreen(
                    state = mapState.value,
                    mapFactory = mapFactory,
                    onLogItemClick = onLogClick,
                    onEnableLocation = onLocationEnableClick,
                    onDisableLocation = onLocationDisableClick,
                )
            }
            composable("log") {
                AprsScreen {
                    LazyColumn {
                        items(logs.value) { log ->
                            AprsLogItem(log, onLogClick)
                        }
                    }
                }
            }
        }

        BottomNavigation {
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
    recordingEnabled: Boolean,
    onRecordingEnableClick: () -> Unit,
    onRecordingDisableClick: () -> Unit,
    internetServiceEnabled: Boolean,
    onInternetServiceDisableClick: () -> Unit,
    onInternetServiceEnableClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        actions = {
            if (recordingEnabled) {
                IconButton(
                    onClick = onRecordingDisableClick,
                ) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = stringResource(R.string.capture_record_disable),
                        tint = AprsTheme.Colors.brand,
                    )
                }
            } else {
                IconButton(
                    onClick = onRecordingEnableClick
                ) {
                    Icon(
                        Icons.Default.MicOff,
                        contentDescription = stringResource(R.string.capture_record_enable),
                    )
                }
            }
            if (internetServiceEnabled) {
                IconButton(
                    onClick = onInternetServiceDisableClick,
                ) {
                    Icon(
                        Icons.Default.Cloud,
                        contentDescription = "Disable APRS-IS",
                        tint = AprsTheme.Colors.brand,
                    )
                }
            } else {
                IconButton(
                    onClick = onInternetServiceEnableClick,
                ) {
                    Icon(
                        Icons.Default.CloudOff,
                        contentDescription = "Disable APRS-IS"
                    )
                }
            }
            IconButton(
                onClick = onSettingsClick
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = stringResource(R.string.settings_title)
                )
            }
        }
    )
}
