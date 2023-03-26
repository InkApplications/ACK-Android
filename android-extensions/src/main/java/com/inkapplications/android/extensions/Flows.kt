package com.inkapplications.android.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@Deprecated("Replace with watermelon util call when 1.1.0 is released")
inline fun <STATE, ITEM> Flow<STATE>.combineApply(other: Flow<ITEM>, crossinline applicator: STATE.(ITEM) -> Unit): Flow<STATE> {
    return combine(other) { state, newItem ->
        state.also { applicator.invoke(state, newItem) }
    }
}
