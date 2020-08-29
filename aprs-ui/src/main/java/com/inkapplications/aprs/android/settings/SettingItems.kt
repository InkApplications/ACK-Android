package com.inkapplications.aprs.android.settings

import com.inkapplications.aprs.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.setting_item_boolean.*
import kotlinx.android.synthetic.main.setting_item_int.*
import kotlinx.android.synthetic.main.setting_item_string.*

class StringSettingItem(val viewModel: StringSettingViewModel, val setting: StringSetting): Item() {
    override fun getId(): Long = setting.hashCode().toLong()
    override fun getLayout(): Int = R.layout.setting_item_string

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.setting_item_string_name.text = viewModel.name
        viewHolder.setting_item_string_value.text = viewModel.value
    }
}

class IntSettingItem(val viewModel: IntSettingViewModel, val setting: IntSetting): Item() {
    override fun getId(): Long = setting.hashCode().toLong()
    override fun getLayout(): Int = R.layout.setting_item_int

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.setting_item_int_name.text = viewModel.name
        viewHolder.setting_item_int_value.text = viewModel.value
    }
}

class BooleanSettingItem(
    val viewModel: BooleanSettingViewModel,
    val setting: BooleanSetting,
    private val onChangeListener: (BooleanSetting, Boolean) -> Unit
): Item() {
    override fun getId(): Long = setting.hashCode().toLong()
    override fun getLayout(): Int = R.layout.setting_item_boolean

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.setting_item_boolean_name.text = viewModel.name
        viewHolder.setting_item_boolean_switch.isChecked = viewModel.value
        viewHolder.setting_item_boolean_switch.setOnCheckedChangeListener { _, checked ->
            onChangeListener(setting, checked)
        }
    }
}
