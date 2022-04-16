package com.inkapplications.ack.android.capture.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import com.inkapplications.ack.android.capture.CaptureEvents
import com.inkapplications.ack.android.component
import kimchi.Kimchi
import kotlinx.coroutines.*

abstract class CaptureService: Service() {
    protected lateinit var runScope: CoroutineScope
    protected lateinit var captureEvents: CaptureEvents
    private lateinit var captureServiceNotifications: CaptureServiceNotifications
    abstract val notificationId: Int
    abstract val notificationTitle: String
    final override fun onBind(intent: Intent): IBinder? = null

    final override fun onCreate() {
        super.onCreate()
        Kimchi.info("onCreate ${this::class.java.simpleName}")
        runScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        captureEvents = component.captureEvents()
        captureServiceNotifications = component.captureServiceNotifications()
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


