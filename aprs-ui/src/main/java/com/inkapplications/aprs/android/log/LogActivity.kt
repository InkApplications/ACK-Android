package com.inkapplications.aprs.android.log

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.aprs.data.AprsAccess
import com.inkapplications.kotlin.collectOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kimchi.Kimchi
import kotlinx.android.synthetic.main.log.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

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

        aprs.data.collectOn(foreground) {
            adapter.add(LogItem(it))
            Kimchi.info("Received APRS Packet: $it")
        }
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}
