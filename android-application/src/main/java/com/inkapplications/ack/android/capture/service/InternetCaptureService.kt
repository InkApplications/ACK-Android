package com.inkapplications.ack.android.capture.service

import com.inkapplications.ack.android.R

class InternetCaptureService: CaptureService() {
    override val notificationId: Int = 2405
    override val notificationTitle: String get() = getString(R.string.capture_service_notification_title_internet)

    override suspend fun run() {
        captureEvents.connectInternet()
    }
}
