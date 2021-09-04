package com.inkapplications.kotlin

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Deprecated("Use Watermelon")
inline fun <T> Flow<T>.collectOn(scope: CoroutineScope, crossinline collector: (T) -> Unit): Job {
    return scope.launch {
        safeCollect { collector(it) }
    }
}

/**
 * Collect a flow after ensuring that the context is currently active.
 */
@Deprecated("Use Watermelon")
suspend inline fun <T> Flow<T>.safeCollect(crossinline action: suspend (T) -> Unit) {
    collect {
        coroutineContext.ensureActive()
        action(it)
    }
}

/**
 * Map each item in the emitted lists for the flow.
 */
@Deprecated("Use Watermelon")
inline fun <T, R> Flow<Collection<T>>.mapEach(crossinline mapping: suspend (T) -> R): Flow<List<R>> {
    return map { it.map { mapping(it) } }
}

/**
 * Filter each list emitted by a flow.
 */
@Deprecated("Use Watermelon")
inline fun <T> Flow<Collection<T>>.filterEach(crossinline predicate: suspend (T) -> Boolean): Flow<List<T>> {
    return map { it.filter { predicate(it) } }
}

/**
 * Filter each list emitted by a flow.
 */
@Deprecated("Use Watermelon")
fun <T: Any> Flow<Collection<T?>>.filterEachNotNull(): Flow<List<T>> {
    return map { it.filterNotNull() }
}

/**
 * Merge a list of flows taking the first flow's latest result that matches a [predicate]
 */
inline fun <reified T> List<Flow<T>>.flattenFirst(crossinline predicate: (T) -> Boolean): Flow<T?> = combine(*this.toTypedArray()) {
    it.firstOrNull { predicate(it) }
}

/**
 * Make a flow of nullable values non-null by mapping nulls to a default given value.
 */
inline fun <T: Any> Flow<T?>.mapNullToDefault(default: T): Flow<T> = map { it ?: default }

/**
 * Make a flow nullable and emit null on start.
 *
 * This is useful when merging flows when you do not want to wait for
 * a flow to emit an item before the merge operation begins.
 */
fun <T> Flow<T>.startNull() = map { it as T? }.onStart { emit(null) }
