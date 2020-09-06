package com.inkapplications.aprs.android

import com.inkapplications.android.extensions.StringResources

/**
 * Spits arguments back out as values for testing.
 */
object ParrotStringResources: StringResources {
    override fun getString(key: Int): String = ""
    override fun getString(key: Int, vararg arguments: Any): String = arguments.joinToString("|")
}
