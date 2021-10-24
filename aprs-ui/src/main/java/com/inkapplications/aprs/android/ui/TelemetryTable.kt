package com.inkapplications.aprs.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inkapplications.aprs.android.ui.theme.AprsTheme
import com.inkapplications.karps.structures.TelemetryValues

@Composable
fun TelemetryTable(values: TelemetryValues, sequence: String?) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = AprsTheme.spacing.content)) {
        Column(modifier = Modifier.padding(AprsTheme.spacing.content)) {
            Text("Telemetry Data", style = AprsTheme.typography.h2)
            TelemetryValueRow("a1", values.analog1.toString())
            TelemetryValueRow("a2", values.analog2.toString())
            TelemetryValueRow("a3", values.analog3.toString())
            TelemetryValueRow("a4", values.analog4.toString())
            TelemetryValueRow("a5", values.analog5.toString())
            TelemetryValueRow("d1", values.digital.toString())
            TelemetryValueRow("sq", sequence.orEmpty())
        }
    }
}

@Composable
private fun TelemetryValueRow(
    label: String,
    value: String,
) = KeyValueRow(label, value, Modifier.padding(vertical = AprsTheme.spacing.singleItem))
