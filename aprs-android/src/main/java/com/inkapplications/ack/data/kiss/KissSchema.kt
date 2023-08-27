package com.inkapplications.ack.data.kiss

/**
 * KISS Protocol signifiers used for parsing.
 *
 * @see http://www.ka9q.net/papers/kiss.html
 */
internal object KissSchema {
    const val FrameEnd = 0xC0
    const val FrameEscape = 0xDB
    const val TransposedFrameEnd = 0xDC
    const val TransposedFrameEscape = 0xDD

    object HostTypes {
        const val Data = 0x00
    }

    object TncTypes {
        const val Data = 0x00
        const val TxDelay = 0x01
        const val Persistence = 0x02
        const val SlotTime = 0x03
        const val TxTail = 0x04
        const val FullDuplex = 0x05
        const val SetHardware = 0x06
        const val Return = 0xFF
    }
}
