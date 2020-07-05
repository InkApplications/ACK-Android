package com.inkapplications.aprs.android.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.component
import com.inkapplications.kotlin.collectOn
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.log.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class LogFragment: Fragment() {
    private lateinit var data: LogDataAccess
    private lateinit var foreground: CoroutineScope
    private val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.log, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        data = component.logData()
        log_list.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        foreground = MainScope()

        data.items.collectOn(foreground) {
            adapter.update(it)
        }
    }

    override fun onStop() {
        foreground.cancel()
        super.onStop()
    }
}
