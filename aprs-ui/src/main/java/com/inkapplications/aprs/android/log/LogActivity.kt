package com.inkapplications.aprs.android.log

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.data.AprsAccess
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.log.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LogActivity: AppCompatActivity() {
    private lateinit var aprs: AprsAccess
    private lateinit var foreground: CoroutineScope
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log)

        aprs = component.aprs()
        log_list.adapter = adapter
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
