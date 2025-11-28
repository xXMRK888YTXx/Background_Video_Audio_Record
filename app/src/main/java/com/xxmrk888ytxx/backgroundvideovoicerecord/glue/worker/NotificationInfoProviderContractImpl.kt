package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.worker.contract.NotificationInfoProviderContract
import javax.inject.Inject

class NotificationInfoProviderContractImpl @Inject constructor(
    private val context: Context
) : NotificationInfoProviderContract {
    override val notificationId: Int = NOTIFICATION_ID_FOR_EXPORT_WORKER
    override val foregroundServiceNotification: Notification
        get() {
            createNotificationChannel()

            return context.buildNotification(CHANNEL_ID_FOR_EXPORT_WORKER) {
                setContentTitle(context.getString(R.string.the_export_is_currently_underway))
                setContentText(context.getString(R.string.do_not_close_the_app))
                setSmallIcon(R.drawable.settings)

            }
        }

    fun createNotificationChannel() {
        if (!isHaveNotificationPermission) return
        context.buildNotificationChannel(CHANNEL_ID_FOR_EXPORT_WORKER,
            context.getString(R.string.export_worker))
    }

    private val isHaveNotificationPermission: Boolean
        get() = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context.checkSelfPermission(POST_NOTIFICATIONS) == PERMISSION_GRANTED else true

    companion object {
        const val NOTIFICATION_ID_FOR_EXPORT_WORKER = 3254

        const val CHANNEL_ID_FOR_EXPORT_WORKER = "CHANNEL_ID_FOR_EXPORT_WORKER"

    }
}