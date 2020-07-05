package com.inkapplications.aprs.android.log

import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.aprs.data.AprsPacket
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.log_item.*

class LogItem(
    val packet: AprsPacket,
    private val symbolFactory: SymbolFactory
): Item() {
    override fun getLayout(): Int = R.layout.log_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.log_origin.text = packet.source?.callsign.orEmpty()
        viewHolder.log_comment.text = packet.comment

        when (packet) {
            is AprsPacket.Location -> viewHolder.log_symbol.setImageBitmap(symbolFactory.createSymbol(packet.symbol))
            else -> viewHolder.log_symbol.setImageResource(R.drawable.symbol_94)
        }
    }
}
