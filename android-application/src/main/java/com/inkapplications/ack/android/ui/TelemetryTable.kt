package com.inkapplications.ack.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.inkapplications.ack.android.ui.theme.AckTheme
import com.inkapplications.ack.structures.TelemetryValues

@Composable
fun TelemetryTable(values: TelemetryValues, sequence: String?) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = AckTheme.dimensions.content)) {
        Column(modifier = Modifier.padding(AckTheme.dimensions.content)) {
            Text("Telemetry Data", style = AckTheme.typography.h2)
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
) = KeyValueRow(label, value, Modifier.padding(vertical = AckTheme.dimensions.singleItem))
