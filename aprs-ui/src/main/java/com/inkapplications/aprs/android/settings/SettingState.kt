package com.inkapplications.aprs.android.settings

sealed interface SettingState {
    val setting: Setting

    data class IntState(
        override val setting: IntSetting,
        val value: Int,
    ): SettingState

    data class StringState(
        override val setting: StringSetting,
        val value: String,
    ): SettingState

    data class BooleanState(
        override val setting: BooleanSetting,
        val value: Boolean,
    ): SettingState
}
