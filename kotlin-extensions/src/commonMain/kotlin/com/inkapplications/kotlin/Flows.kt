package com.inkapplications.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

inline fun <T> Flow<T>.collectOn(scope: CoroutineScope, crossinline collector: (T) -> Unit): Job {
    return scope.launch {
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

/**
 * Map each item in the emitted lists for the flow.
 */
inline fun <T, R> Flow<Collection<T>>.mapEach(crossinline mapping: suspend (T) -> R): Flow<List<R>> {
    return map { it.map { mapping(it) } }
}
