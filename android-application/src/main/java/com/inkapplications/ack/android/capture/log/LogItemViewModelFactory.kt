package com.inkapplications.ack.android.capture.log

import com.inkapplications.karps.structures.AprsPacket

interface LogItemViewModelFactory {
    fun create(
        id: Long,
        packet: AprsPacket,
        metric: Boolean,
    ): LogItemViewModel
}
