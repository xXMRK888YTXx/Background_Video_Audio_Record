package com.xxmrk888ytxx.worker.contract

import android.app.Notification

interface NotificationInfoProviderContract {
    val notificationId: Int
    val foregroundServiceNotification: Notification
}