package com.inkapplications.aprs.android.settings

/**
 * Definition of an individual configuration aspect of the application.
 */
sealed class Setting {
    abstract val key: String
    abstract val name: String
    abstract val categoryName: String
    abstract val defaultValue: Any
    abstract val advanced: Boolean
}

data class StringSetting(
    override val key: String,
    override val name: String,
    override val categoryName: String,
    override val defaultValue: String,
    override val advanced: Boolean = false
): Setting()

data class IntSetting(
    override val key: String,
    override val name: String,
    override val categoryName: String,
    override val defaultValue: Int,
    override val advanced: Boolean = false
): Setting()