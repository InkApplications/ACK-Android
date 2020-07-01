package com.inkapplications.aprs.android.log

import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.data.AprsPacket
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.log_item.*

class LogItem(val packet: AprsPacket): Item() {
    override fun getLayout(): Int = R.layout.log_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.log_statement.text = "${packet.source?.callsign} ➡️ ${packet.destination?.callsign}: ${packet.comment}"
    }
}
