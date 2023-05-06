package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.getSystemService
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.models.DelayRecordTask
import com.xxmrk888ytxx.backgroundvideovoicerecord.receivers.ExecuteDelayRecordTaskReceiver
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DelayStartRecordManagerImpl @Inject constructor(
    private val context: Context,
    private val preferencesStorage: PreferencesStorage
) : DelayStartRecordManager {

    private val delayRecordTaskKey = stringPreferencesKey("delayRecordTaskKey")

    private val alarmManager:AlarmManager by lazy {
        context.getSystemService<AlarmManager>()!!
    }

    private val intent : Intent
        get() = Intent(context,ExecuteDelayRecordTaskReceiver::class.java).apply {
            action = ExecuteDelayRecordTaskReceiver.executeDelayRecordTaskAction
        }

    private val pendingIntent : PendingIntent
        get() = PendingIntent.getBroadcast(context,0,intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


    override suspend fun setDelayAudioRecord(time: Long) {
        Log.i(LOG_TAG,"setDelayAudioRecord request")

        val jsonString = try {
            Json.encodeToString(DelayRecordTask.serializer(),
                DelayRecordTask(DelayRecordTask.AUDIO_RECORD_TYPE,time)
            )
        }catch (e:Exception) {
            Log.e(LOG_TAG,"Error serialization DelayRecordTask to json. ${e.stackTraceToString()}")
            return
        }

        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(delayRecordTaskKey,jsonString)
        }

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(time,pendingIntent),pendingIntent)
        Log.i(LOG_TAG,"alarm for delay Audio Record setup. AlarmTime:$time")
    }

    override suspend fun setDelayVideoRecord(time: Long) {
        Log.i(LOG_TAG,"setDelayVideoRecord request")

        val jsonString = try {
            Json.encodeToString(DelayRecordTask.serializer(),
                DelayRecordTask(DelayRecordTask.VIDEO_RECORD_TYPE,time)
            )
        }catch (e:Exception) {
            Log.e(LOG_TAG,"Error serialization DelayRecordTask to json. ${e.stackTraceToString()}")
            return
        }

        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(delayRecordTaskKey,jsonString)
        }

        alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(time,pendingIntent),pendingIntent)
        Log.i(LOG_TAG,"alarm for delay video record setup. AlarmTime:$time")
    }

    override suspend fun cancelDelayTask() {
        Log.i(LOG_TAG,"cancelDelayTask request")
        withContext(Dispatchers.IO) {
            preferencesStorage.removeProperty(delayRecordTaskKey)
        }

        val currentAlarm = alarmManager.nextAlarmClock

        if(currentAlarm == null) {
            Log.i(LOG_TAG,"Alarm not setup")
        } else {
            alarmManager.cancel(currentAlarm.showIntent)
            Log.i(LOG_TAG,"Alarm installed at ${currentAlarm.triggerTime} canceled")
        }
    }

    override suspend fun restoreDelayTask() {
        Log.i(LOG_TAG,"restoreDelayTask request")

        val currentTask = currentTask.first()

        if(currentTask == null) {
            Log.i(LOG_TAG,"Current task is null. Restore not possible")
        } else {
            when(currentTask.type) {
                DelayRecordTask.AUDIO_RECORD_TYPE -> setDelayAudioRecord(currentTask.time)

                DelayRecordTask.VIDEO_RECORD_TYPE -> setDelayVideoRecord(currentTask.time)
            }

            Log.i(LOG_TAG,"Task restored")
        }
    }

    override val currentTask: Flow<DelayRecordTask?> = preferencesStorage
        .getPropertyOrNull(delayRecordTaskKey)
        .map {
            try {
                it?.run {
                    Json.decodeFromString(DelayRecordTask.serializer(),this)
                }
            }catch (_:Exception) {
                null
            }
        }

    companion object {
        const val LOG_TAG = "DelayStartRecordManagerImpl"
    }
}