package com.inkapplications.ack.android.capture.log

import com.inkapplications.ack.structures.AprsPacket

interface LogItemViewModelFactory {
    fun create(
        id: Long,
        packet: AprsPacket,
        metric: Boolean,
    ): LogItemViewModel
}
