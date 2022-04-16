package com.inkapplications.android.extensions

import android.app.Notification
import android.content.Context
import android.os.Build

/**
 * Create a notification builder with a channel ID if the OS supports it.
 */
fun Context.notificationBuilder(channelId: String): Notification.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(this, channelId)
    } else {
        Notification.Builder(this)
    }
}
