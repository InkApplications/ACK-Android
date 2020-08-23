package com.inkapplications.aprs.android.capture.log

import android.view.View
import com.inkapplications.aprs.android.R
import com.inkapplications.aprs.android.symbol.SymbolFactory
import com.inkapplications.karps.structures.AprsPacket
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.log_item.view.*

class LogItem(
    val packet: AprsPacket,
    private val symbolFactory: SymbolFactory
): Item(packet.hashCode().toLong()) {
    override fun getLayout(): Int = R.layout.log_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindToView(viewHolder.containerView)
    }

    fun bindToView(container: View) {
        container.log_origin.text = packet.source.toString()
        container.log_comment.text = when (packet) {
            is AprsPacket.Position -> packet.comment
            is AprsPacket.Weather -> "🌡 ${packet.temperature}"
            is AprsPacket.Unknown -> "⚠️ ${packet.body}"
        }

        when (packet) {
            is AprsPacket.Position -> packet.symbol.let(symbolFactory::createSymbol).run(container.log_symbol::setImageBitmap)
            is AprsPacket.Weather -> packet.symbol?.let(symbolFactory::createSymbol).run(container.log_symbol::setImageBitmap)
            else -> container.log_symbol.setImageResource(R.drawable.symbol_94)
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is LogItem) return false
        return packet == other.packet
    }
}
