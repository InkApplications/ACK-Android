package com.inkapplications.ack.data

/*
The following file is based off of jsoundmodem.
It has been modified extensively, ported to kotlin, and is redistributed under
the GNU General Public License v2.0.
The original source code was obtained from https://github.com/nogy/jsoundmodem.
 */
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.os.Process
import inkapplications.spondee.scalar.Percentage
import inkapplications.spondee.structure.toFloat
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.or
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.time.Duration

private const val FREQ_LOW = 1200
private const val FREQ_HIGH = 2200
private const val BPS = 1200
private const val SAMPLE_RATE = 22050
private const val PCM_BITS = 16

internal class AndroidAfskModulator {
    private val trackScope = CoroutineScope(SupervisorJob() + newSingleThreadContext("AudioTrack"))

    fun modulate(data: ByteArray, length: Duration, volume: Percentage) {
        val crc = crc16(data)
        val (frame, frameLength) = frame(data + crc.first + crc.second, (length.inWholeMilliseconds * BPS/8/1000).toInt())
        val pcmData = ShortArray(frameLength.times(SAMPLE_RATE).div(BPS))
        trackScope.launch {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)

            var lastTone = FREQ_LOW
            var cosPos = 0.0
            for (i in 0 until frameLength) {
                if (frame[i/8].toInt().and(1.shl(i % 8)) == 0) {
                    lastTone = if (lastTone == FREQ_LOW) FREQ_HIGH else FREQ_LOW
                }
                for (t in 0 until SAMPLE_RATE / BPS) {
                    pcmData[i.times(SAMPLE_RATE / BPS).plus(t)] = cos(cosPos).times(1.shl(PCM_BITS-1) - 1).roundToInt().toShort()
                    cosPos += 2 * PI * lastTone / SAMPLE_RATE
                    if (cosPos > 2 * PI) {
                        cosPos -= 2 * PI
                    }
                }
            }

            send(pcmData, volume)
        }
    }

    private suspend fun send(pcmData: ShortArray, volume: Percentage) {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioTrack(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE).build(),
                AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).setSampleRate(SAMPLE_RATE).build(),
                pcmData.size * 2,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE,
            )
        } else AudioTrack(
            AudioManager.STREAM_RING,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            pcmData.size * 2,
            AudioTrack.MODE_STATIC,
        )

        suspendCoroutine<Unit> { continuation ->
            track.setPlaybackPositionUpdateListener(object: AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(track: AudioTrack) {
                    track.release()
                    continuation.resume(Unit)
                }
                override fun onPeriodicNotification(track: AudioTrack?) = Unit
            })
            track.notificationMarkerPosition = pcmData.size
            val volume = AudioTrack.getMaxVolume() * volume.toDecimal().toFloat()
            track.setStereoVolume(volume, volume)
            track.write(pcmData, 0, pcmData.size)
            runBlocking {
                // Without this delay the audiotrack will automatically pause shortly after playing. I hate this, but cannot figure out an alternative.
                delay(500)
            }
            track.play()
        }
    }

    private fun crc16(data: ByteArray): Pair<Byte, Byte> {
        return data.fold(0xFFFF) { crc, byte ->
                (0 until 8).fold(crc) { bitCrc, position ->
                    if (bitCrc.and(1) != byte.toInt().and(1.shl(position)).shr(position)) {
                        bitCrc.shr(1).xor(0x8408).and(0xFFFF)
                    } else {
                        bitCrc.shr(1)
                    }
                }
            }
            .xor(0xFFFF).let {
                Pair(
                    it.and(0xFF).minus(256).toByte(),
                    it.and(0xFF00).shr(8).minus(256).toByte(),
                )
            }
    }

    private fun frame(data: ByteArray, length: Int): Pair<ByteArray, Int> {
        val out = ByteArray(data.size + length + Math.ceil(data.size / 5.0).toInt() + 1)
        for (i in 0 until length ) {
            out[i] = 0x7e
        }

        var ones = 0
        var k = length*8
        for (i in 0 until data.size*8) {
            if (data[i / 8].toInt() and (1 shl i % 8) > 0) {
                out[k / 8] = out[k / 8] or if (1 shl k % 8 > 127) ((1 shl k % 8) - 256).toByte() else (1 shl k % 8).toByte()
                if (ones++ == 4) {
                    k++
                    ones = 0
                }
            } else {
                ones = 0
            }
            k++
        }

        for (i in 0 until 8) {
            if (0x7e and (1 shl i % 8) > 0) {
                out[k / 8] = out[k / 8] or if (1 shl k % 8 > 127) ((1 shl k % 8) - 256).toByte() else (1 shl k % 8).toByte()
            }
            k++
        }

        return out to k+1
    }
}
