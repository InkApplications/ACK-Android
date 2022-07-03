package com.inkapplications.android.extensions

import android.content.res.Resources
import androidx.annotation.IntegerRes
import dagger.Reusable
import javax.inject.Inject

/**
 * Provides access for locating integer configurations in resources.
 *
 * Using this instead of Android's resources makes testing easier.
 */
interface IntegerResources {
    /**
     * Get an integer from resources.
     */
    fun getInteger(@IntegerRes key: Int): Int
}

/**
 * Use Android's Resource class to fetch integer resources
 */
@Reusable
class AndroidIntegerResources @Inject constructor(private val resources: Resources): IntegerResources {
    override fun getInteger(key: Int): Int = resources.getInteger(key)
}

/**
 * Stub implementation of integer resources, used for testing.
 */
object StubIntegerResources: IntegerResources {
    override fun getInteger(key: Int): Int = TODO("stub")
}
