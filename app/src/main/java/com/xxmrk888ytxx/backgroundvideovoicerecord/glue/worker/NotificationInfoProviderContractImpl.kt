package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.annotation.RequiresApi
import com.xxmrk888ytxx.backgroundvideovoicerecord.R
import com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.MainActivity
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
        context.buildNotificationChannel(
            CHANNEL_ID_FOR_EXPORT_WORKER,
            context.getString(R.string.export_worker)
        )
    }

    override fun showFolderForExportRemovedNotification() {
        createNotificationChannel()
        val notification = context.buildNotification(CHANNEL_ID_FOR_EXPORT_WORKER) {
            setContentTitle(context.getString(R.string.the_folder_for_export_is_unavailable))
            setContentText(context.getString(R.string.specify_the_folder_to_export_again))
            setSmallIcon(R.drawable.settings)
            setContentIntent(createLaunchAppIntent())
            setAutoCancel(true)
        }
        try {
            context.getSystemService(NotificationManager::class.java)
                .notify(ERROR_NOTIFICATION_ID_FOR_EXPORT_WORKER, notification)
        } catch (_: Exception) {
        }
    }

    private fun createLaunchAppIntent(): PendingIntent {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val isHaveNotificationPermission: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) context.checkSelfPermission(
            POST_NOTIFICATIONS
        ) == PERMISSION_GRANTED else true

    companion object {
        const val NOTIFICATION_ID_FOR_EXPORT_WORKER = 3254

        const val ERROR_NOTIFICATION_ID_FOR_EXPORT_WORKER = 342385

        const val CHANNEL_ID_FOR_EXPORT_WORKER = "CHANNEL_ID_FOR_EXPORT_WORKER"

    }
}