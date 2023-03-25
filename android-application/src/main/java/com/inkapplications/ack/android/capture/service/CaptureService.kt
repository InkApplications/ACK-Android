package com.inkapplications.ack.android.capture.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import com.inkapplications.ack.android.capture.CaptureEvents
import dagger.hilt.android.AndroidEntryPoint
import kimchi.Kimchi
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Service used for capturing packets in the background.
 */
@AndroidEntryPoint
abstract class CaptureService: Service() {
    @Inject
    protected lateinit var captureEvents: CaptureEvents

    @Inject
    lateinit var captureServiceNotifications: CaptureServiceNotifications

    protected lateinit var runScope: CoroutineScope
    abstract val notificationId: Int
    abstract val notificationTitle: String
    final override fun onBind(intent: Intent): IBinder? = null

    final override fun onCreate() {
        super.onCreate()
        Kimchi.info("onCreate ${this::class.java.simpleName}")
        runScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    final override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Kimchi.debug("Starting Capture Service")

        val notification = captureServiceNotifications.createServiceNotification(this, notificationTitle)
        startForeground(notificationId, notification)

        runScope.launch { run() }

        return super.onStartCommand(intent, flags, startId)
    }

    abstract suspend fun run()

    final override fun onDestroy() {
        Kimchi.info("onDestroy ${this::class.java.simpleName}")
        runScope.cancel()
        super.onDestroy()
    }
}


