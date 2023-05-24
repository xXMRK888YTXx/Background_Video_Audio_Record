package com.xxmrk888ytxx.recordvideoservice

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon

internal object ServiceNotificationActions {


    fun createActionForPauseRecord(
        context: Context
    ) : Notification.Action {

        val intent = Intent(VIDEO_RECORD_SERVICE_COMMAND_ACTION).apply {
            putExtra(COMMAND_KEY,PAUSE_RECORD_ACTION)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, PAUSE_RECORD_ACTION.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Action.Builder(
            Icon.createWithResource(context,R.drawable.pause),
            context.getString(R.string.Pause_Recording),
            pendingIntent
        ).build()
    }

    fun createActionForResumeRecord(
        context: Context
    ) : Notification.Action {

        val intent = Intent(VIDEO_RECORD_SERVICE_COMMAND_ACTION).apply {
            putExtra(COMMAND_KEY, RESUME_RECORD_ACTION)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, RESUME_RECORD_ACTION.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Action.Builder(
            Icon.createWithResource(context,R.drawable.play),
            context.getString(R.string.Resume_recording),
            pendingIntent
        ).build()
    }

    fun createActionForStopRecord(
        context: Context
    ) : Notification.Action {

        val intent = Intent(VIDEO_RECORD_SERVICE_COMMAND_ACTION).apply {
            putExtra(COMMAND_KEY, STOP_RECORD_ACTION)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, STOP_RECORD_ACTION.hashCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT
                    or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Action.Builder(
            Icon.createWithResource(context,R.drawable.baseline_stop_circle_24),
            context.getString(R.string.Stop_Recording),
            pendingIntent
        ).build()
    }

    const val VIDEO_RECORD_SERVICE_COMMAND_ACTION = "VIDEO_RECORD_SERVICE_COMMAND_ACTION"

    const val COMMAND_KEY = "COMMAND_KEY"

    const val STOP_RECORD_ACTION = "STOP_RECORD_ACTION"
    const val PAUSE_RECORD_ACTION = "PAUSE_RECORD_ACTION"
    const val RESUME_RECORD_ACTION = "RESUME_RECORD_ACTION"
}