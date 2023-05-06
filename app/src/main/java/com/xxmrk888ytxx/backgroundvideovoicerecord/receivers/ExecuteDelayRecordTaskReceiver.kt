package com.xxmrk888ytxx.backgroundvideovoicerecord.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.DelayStartRecordManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.models.DelayRecordTask
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.appComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class ExecuteDelayRecordTaskReceiver : BroadcastReceiver() {

    @Inject
    lateinit var delayStartRecordManager: DelayStartRecordManager

    @Inject
    @ApplicationScopeQualifier
    lateinit var applicationScope:CoroutineScope

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(LOG_TAG,"onReceive:context:$context intentAction:${intent?.action}")
        if(context == null) {
            Log.i(LOG_TAG,"Context is null. Work is not possible")
            return
        }
        context.appComponent.inject(this)
        applicationScope.launch {
            if(intent?.action == executeDelayRecordTaskAction) {
                val currentTask = delayStartRecordManager.currentTask.first()
                if(currentTask == null) {
                    Log.i(LOG_TAG,"Tast is null")
                    return@launch
                }

                when(currentTask.type) {
                    DelayRecordTask.AUDIO_RECORD_TYPE -> {
                        Log.i(LOG_TAG,"Execute delay audio record request")
                    }

                    DelayRecordTask.VIDEO_RECORD_TYPE -> {
                        Log.i(LOG_TAG,"Execute delay video record request")
                    }
                }
            }
        }
    }

    companion object {
        const val executeDelayRecordTaskAction = "ExecuteDelayRecordTaskAction"

        const val LOG_TAG = "ExecuteDelayRecordTaskReceiver"
    }

}