package com.inkapplications.android.extensions

import android.content.res.Resources
import androidx.annotation.StringRes
import dagger.Reusable
import javax.inject.Inject

/**
 * Interface for locating string resources.
 *
 * Using this instead of Android's resource class makes testing
 * considerably easier.
 */
interface StringResources {
    fun getString(@StringRes key: Int): String
    fun getString(@StringRes key: Int, vararg arguments: Any): String
}

/**
 * Load String resources via Android's default resource locator.
 */
@Reusable
class AndroidStringResources @Inject constructor(private val resources: Resources): StringResources {
    override fun getString(key: Int): String = resources.getString(key)
    override fun getString(key: Int, vararg arguments: Any) = resources.getString(key, *arguments)
}

/**
 * Stub resources, used for testing.
 */
object StubStringResources: StringResources {
    override fun getString(key: Int): String = TODO("Stub")
    override fun getString(key: Int, vararg arguments: Any): String = TODO("Stub")
}
