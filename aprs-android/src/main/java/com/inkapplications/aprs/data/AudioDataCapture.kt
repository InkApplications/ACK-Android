package com.inkapplications.aprs.data

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Process
import kimchi.logger.KimchiLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel

private const val BUFFER_SIZE = 16
private const val SAMPLE_RATE = 22050
private const val AUDIO_BUFFER_SIZE = 16384
private const val PROCESS_BUFFER_SIZE = 8192

/**
 * Capture buffered audio data on a separate prioritized thread.
 *
 * Data from this capture can be processed by consuming the [audioData] channel.
 */
internal class AudioDataCapture(
    private val logger: KimchiLogger
) {
    private val recorder: AudioRecord = AudioRecord(
        MediaRecorder.AudioSource.DEFAULT,
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        AUDIO_BUFFER_SIZE
    )
    private val captureScope = CoroutineScope(SupervisorJob() + newSingleThreadContext("AudioCapture"))
    private val buffers = Array(BUFFER_SIZE) { ShortArray(PROCESS_BUFFER_SIZE) }
    private val audioChannel = BroadcastChannel<ShortArray>(BUFFER_SIZE)

    /**
     * Outputs buffered arrays of audio data.
     *
     * Note: The arrays from this channel are recycled and should not be stored
     * directly. This channel has a limited capacity of [BUFFER_SIZE] arrays.
     */
    val audioData: ReceiveChannel<ShortArray> get() = audioChannel.openSubscription()

    /**
     * Start capturing audio data.
     *
     * This will suspend until [cancel] is called.
     */
    fun capture() {
        logger.debug("Audio capture started")
        captureScope.launch {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO)
            recorder.startRecording()

            var bufferIndex = 0
            while (recorder.recordingState != AudioRecord.RECORDSTATE_STOPPED) {
                val buffer = buffers[bufferIndex]
                recorder.read(buffer, 0, buffer.size)
                bufferIndex = if (bufferIndex == buffers.size - 1) 0 else bufferIndex + 1
                audioChannel.offer(buffer)
            }
        }
    }

    /**
     * Stop capturing audio data.
     */
    fun cancel() {
        logger.debug("Audio capture stopped")
        recorder.stop()
    }
}
