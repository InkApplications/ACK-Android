package com.inkapplications.aprs.data

/**
 * API for interfacing with the JNI Multimon code.
 */
internal object Multimon {
    /**
     * Invoked when data is parsed from processing.
     */
    var onReceive: (ByteArray) -> Unit = {}

    init {
        System.loadLibrary("multimon")
    }

    /**
     * Initialize the Multimon library.
     */
    external fun init()

    /**
     * Process an array of audio data.
     */
    external fun process(buffer: FloatArray, length: Int)

    private fun callback(data: ByteArray) {
        onReceive(data)
    }
}
