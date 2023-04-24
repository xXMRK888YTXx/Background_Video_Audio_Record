package com.xxmrk888ytxx.coreandroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService

fun Long.milliSecondToString(): String {
    val seconds = this / 1000

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val second = seconds % 60

    val stringConverter: (Long) -> String = {
        if (it < 10) "0$it" else it.toString()
    }

    return "${stringConverter(hours)}:${stringConverter(minutes)}:${stringConverter(second)}"
}

inline fun Context.buildNotificationChannel(
    id: String,
    name: String,
    configuration: NotificationChannel.() -> Unit = {}
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply(configuration)

        val notificationManager = getSystemService<NotificationManager>()

        notificationManager?.createNotificationChannel(channel)
    }
}

inline fun Context.buildNotification(
    channelId:String,
    configuration:Notification.Builder.() -> Unit
) : Notification {
    val notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Notification.Builder(this,channelId)
    } else {
        Notification.Builder(this)
    }

    return notificationBuilder.apply(configuration).build()
}