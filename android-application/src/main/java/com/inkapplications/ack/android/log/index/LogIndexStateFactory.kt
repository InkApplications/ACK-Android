package com.inkapplications.ack.android.log.index

import com.inkapplications.ack.android.log.LogItemViewStateFactory
import dagger.Reusable
import javax.inject.Inject

/**
 * Create Index State objects from log data.
 */
@Reusable
class LogIndexStateFactory @Inject constructor(
    private val stateFactory: LogItemViewStateFactory,
) {
    fun create(data: LogIndexData): LogIndexState {
        return data.packets
            .ifEmpty { return LogIndexState.Empty }
            .let { stateFactory.create(it, data.metric) }
            .let { LogIndexState.LogList(it) }
    }
}
