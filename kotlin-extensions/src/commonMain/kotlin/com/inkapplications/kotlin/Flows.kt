package com.inkapplications.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

inline fun <T> Flow<T>.collectOn(scope: CoroutineScope, crossinline collector: (T) -> Unit) {
    scope.launch {
        safeCollect { collector(it) }
    }
}

/**
 * Collect a flow after ensuring that the context is currently active.
 */
suspend inline fun <T> Flow<T>.safeCollect(crossinline action: suspend (T) -> Unit) {
    collect {
        coroutineContext.ensureActive()
        action(it)
    }
}
