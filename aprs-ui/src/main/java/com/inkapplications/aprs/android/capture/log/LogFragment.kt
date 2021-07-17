package com.inkapplications.aprs.android.capture.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.inkapplications.aprs.android.AprsTheme
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.android.station.startStationActivity

class LogFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(context!!).apply {
            setContent {
                val listState = component.logData().logViewModels.collectAsState(emptyList())
                AprsTheme {
                    LazyColumn {
                        items(listState.value) { log ->
                            AprsLogItem(log, ::onLogClick)
                        }
                    }
                }
            }
        }
    }

    private fun onLogClick(log: LogItemState) {
        activity!!.startStationActivity(log.id)
    }
}
