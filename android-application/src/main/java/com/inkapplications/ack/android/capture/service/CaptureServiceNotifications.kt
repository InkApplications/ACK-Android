package com.inkapplications.ack.android.capture.service

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import com.inkapplications.ack.android.R
import com.inkapplications.ack.android.capture.CaptureActivity
import com.inkapplications.ack.android.startup.ApplicationInitializer
import com.inkapplications.android.extensions.notificationBuilder
import kimchi.logger.KimchiLogger
import javax.inject.Inject

private const val CHANNEL_ID = "ack.capture.services"

/**
 * Creates notification channels for use with the capture service at app start.
 */
class CaptureServiceNotifications @Inject constructor(
    private val notificationManager: NotificationManager,
    private val logger: KimchiLogger,
): ApplicationInitializer {
    override suspend fun initialize(application: Application) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            logger.debug("Skipping notification channel setup")
            return
        }
        logger.debug("Creating Capture Service Notification Channel")
        val channel = NotificationChannel(CHANNEL_ID, application.getString(R.string.capture_service_channel_name), NotificationManager.IMPORTANCE_LOW)
        channel.description = application.getString(R.string.capture_service_channel_description)
        notificationManager.createNotificationChannel(channel)
    }

    fun createServiceNotification(context: Context, title: String): Notification {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = Intent(context, CaptureActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(context, 0, notificationIntent, flags)
        }

        return context.notificationBuilder(CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.capture_service_notification_icon)
            .setContentIntent(pendingIntent)
            .build()
    }
}
