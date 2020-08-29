package com.inkapplications.aprs.android.settings

import com.inkapplications.aprs.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.setting_item_int.*

class SettingIntItem(val viewModel: IntSettingViewModel, val setting: IntSetting): Item() {
    override fun getId(): Long = setting.hashCode().toLong()
    override fun getLayout(): Int = R.layout.setting_item_int

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.setting_item_int_name.text = viewModel.name
        viewHolder.setting_item_int_value.text = viewModel.value
    }
}
