package com.inkapplications.ack.android.capture.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.CaptureEvents
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Service that runs in the background to collect and transmit packets.
 */
@AndroidEntryPoint
open class BackgroundCaptureService: Service() {
    private lateinit var runScope: CoroutineScope

    @Inject
    lateinit var captureEvents: CaptureEvents

    @Inject
    lateinit var captureServiceNotifications: CaptureServiceNotifications
    private val notificationId: Int = 89
    private val notificationTitle: String get() = getString(R.string.capture_service_notification_title)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Kimchi.info("onCreate BackgroundCaptureService")
        Kimchi.trackEvent("background_capture_start")
        runScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = captureServiceNotifications.createServiceNotification(this, notificationTitle)
        startForeground(notificationId, notification)
        runScope.launch {
            captureEvents.connectDriver()
            captureEvents.locationTransmitLoop()
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Kimchi.trackEvent("background_capture_destroy")
        runScope.cancel()
        super.onDestroy()
    }
}

class BackgroundCaptureServiceAudio: BackgroundCaptureService()
