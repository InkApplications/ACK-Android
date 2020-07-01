package com.inkapplications.aprs.android

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.inkapplications.aprs.data.AndroidAprsModule
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.dashboard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardActivity: Activity() {
    private val aprs = AndroidAprsModule.aprsAccess
    private lateinit var foreground: CoroutineScope
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        dashboard_log.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()

        foreground.launch {
            aprs.data.collect {
                adapter.add(LogItem(it))
                Log.i("APRS", "Packet from ${it.source} sent to ${it.destination}")
            }
        }
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}
