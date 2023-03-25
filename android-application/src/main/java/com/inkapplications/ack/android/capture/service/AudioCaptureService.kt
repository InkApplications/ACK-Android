package com.inkapplications.ack.android.capture.service

import com.inkapplications.ack.android.R

/**
 * Background Android service used for capturing audio packets.
 */
class AudioCaptureService: CaptureService() {
    override val notificationId: Int = 44
    override val notificationTitle: String get() = getString(R.string.capture_service_notification_title_audio)

    override suspend fun run() {
        captureEvents.connectAudio()
    }
}
