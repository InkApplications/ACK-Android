package com.inkapplications.aprs.android.settings

sealed interface SettingState {
    val key: String
    val name: String

    data class IntState(
        override val key: String,
        override val name: String,
        val value: Int,
    ): SettingState

    data class StringState(
        override val key: String,
        override val name: String,
        val value: String,
    ): SettingState

    data class BooleanState(
        override val key: String,
        override val name: String,
        val value: Boolean,
    ): SettingState
}
