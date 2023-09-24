package com.inkapplications.ack.data

import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.scalar.decimalPercentage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlin.math.abs
import kotlin.math.absoluteValue

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
        multimon.onReceive = { trySend(it) }

        audioIn.capture()

        audioIn.audioData.consumeEach { decode(it) }
        awaitClose {
            audioIn.cancel()
            peak.value = null
        }
    }

    private val peak = MutableStateFlow<Short?>(null)
    val volume: Flow<Percentage?> = peak.map {
        it?.toDouble()?.absoluteValue?.div(Short.MAX_VALUE)?.decimalPercentage
    }

    private fun decode(audioData: ShortArray) {
        peak.value = audioData.maxOf { abs(it.toInt()) }.toShort()
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

