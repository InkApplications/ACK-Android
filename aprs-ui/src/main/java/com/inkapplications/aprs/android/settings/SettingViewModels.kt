package com.inkapplications.aprs.android.settings

data class IntSettingViewModel(
    val name: String,
    val value: String
) {
    constructor(setting: IntSetting, value: Int?): this(setting.name, value?.toString().orEmpty())
}

data class StringSettingViewModel(
    val name: String,
    val value: String
) {
    constructor(setting: StringSetting, value: String?): this(setting.name, value.orEmpty())
}
