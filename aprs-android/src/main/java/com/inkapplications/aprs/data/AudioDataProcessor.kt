package com.inkapplications.aprs.data

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.callbackFlow

private const val overlap = 18

internal class AudioDataProcessor(
    private val audioIn: AudioDataCapture
) {
    private val multimon by lazy {
        Multimon.apply {
            init()
        }
    }

    private val fbuf = FloatArray(16384)
    private var fbuf_cnt = 0

    val data = callbackFlow {
        multimon.onReceive = { offer(it) }

        audioIn.capture()

        invokeOnClose {
            audioIn.cancel()
        }
        audioIn.audioData.consumeEach { decode(it) }
    }

    fun decode(audioData: ShortArray) {
        for (i in audioData.indices) {
            fbuf[fbuf_cnt++] = audioData[i] * (1.0f / 32768.0f)
        }

        if (fbuf_cnt > overlap) {
            multimon.process(fbuf, fbuf_cnt - overlap)
            System.arraycopy(fbuf, fbuf_cnt - overlap, fbuf, 0, overlap)
            fbuf_cnt = overlap
        }
    }
}

