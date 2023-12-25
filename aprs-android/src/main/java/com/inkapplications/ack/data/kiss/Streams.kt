package com.inkapplications.ack.data.kiss

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.resume

/**
 * Extract frame data from a serial stream via the KISS protocol.
 *
 * @see http://www.ka9q.net/papers/kiss.html
 */
fun InputStream.kissData(): Flow<ByteArray> {
    return flow {
        val buffer = ArrayList<Byte>()
        while (currentCoroutineContext().isActive) {
            when (val char = readSuspend()) {
                KissSchema.FrameEnd -> {
                    if (buffer.isNotEmpty()) {
                        val command = buffer[0]

                        when (command) {
                            KissSchema.HostTypes.Data.toByte() -> {
                                emit(buffer.drop(1).toByteArray())
                                buffer.clear()
                            }
                        }
                    }
                }
                KissSchema.FrameEscape -> when (readSuspend()) {
                    KissSchema.TransposedFrameEnd -> buffer += KissSchema.FrameEnd.toByte()
                    KissSchema.TransposedFrameEscape -> buffer += KissSchema.FrameEscape.toByte()
                }
                else -> buffer += char.toByte()
            }
        }
    }
}

/**
 * Write a byte array to the stream via the KISS protocol.
 *
 * @param bytes The data to write.
 * @param type The type of frame the data bytes represent.
 * @see http://www.ka9q.net/papers/kiss.html
 */
fun OutputStream.writeKissData(
    bytes: ByteArray,
    type: Int = KissSchema.TncTypes.Data,
) {
    write(KissSchema.FrameEnd)
    write(type)
    write(bytes)
    write(KissSchema.FrameEnd)
    flush()
}

/**
 * Read a single byte from the stream, suspending on the IO thread until
 * it is available.
 */
private suspend fun InputStream.readSuspend(): Int {
    return withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { cont ->
            cont.invokeOnCancellation { close() }
            cont.resume(read())
        }
    }
}
