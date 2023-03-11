package com.inkapplications.ack.android.log

import com.inkapplications.ack.structures.AprsPacket

interface LogItemViewStateFactory {
    fun create(
        id: Long,
        packet: AprsPacket,
        metric: Boolean,
    ): LogItemViewState
}
