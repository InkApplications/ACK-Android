package com.inkapplications.aprs.android.capture.log

import android.view.View
import com.inkapplications.aprs.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.log_item.view.*

class LogItem(
    id: Long,
    val viewModel: LogViewModel
): Item(id) {
    override fun getLayout(): Int = R.layout.log_item

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        bindToView(viewHolder.containerView)
    }

    fun bindToView(container: View) {
        container.log_origin.text = viewModel.origin
        container.log_comment.text = viewModel.comment
        container.log_symbol.setImageBitmap(viewModel.symbol)
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is LogItem) return false
        return viewModel == other.viewModel
    }
}
