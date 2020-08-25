package com.inkapplications.android.extensions

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * An Activity with some generic add-ons.
 */
abstract class ExtendedActivity: AppCompatActivity() {
    /**
     * Only run while the screen is in the foreground, aka started.
     */
    protected lateinit var foregroundScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()

        if (savedInstanceState == null) {
            onFirstCreate()
        } else {
            onRestore(savedInstanceState)
        }
    }

    @CallSuper
    open fun onCreate() {}

    @CallSuper
    open fun onRestore(savedInstanceState: Bundle) {}

    @CallSuper
    protected open fun onFirstCreate() {}

    override fun onStart() {
        super.onStart()
        foregroundScope = MainScope()
    }

    override fun onStop() {
        foregroundScope.cancel()
        super.onStop()
    }
}