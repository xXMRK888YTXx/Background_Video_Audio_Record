package com.xxmrk888ytxx.backgroundvideovoicerecord.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.qualifiers.ApplicationScopeQualifier
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.DelayStartRecordManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.models.DelayRecordTask
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.utils.appComponent
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
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

    @Inject
    lateinit var audioRecordServiceManager: AudioRecordServiceManager

    @Inject
    lateinit var videoRecordServiceManager: VideoRecordServiceManager

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

                val currentAudioRecordState = audioRecordServiceManager.currentRecordState.first()
                val currentVideoRecordState = videoRecordServiceManager.currentRecordState.first()

                when(currentTask.type) {
                    DelayRecordTask.AUDIO_RECORD_TYPE -> {
                        Log.i(LOG_TAG,"Execute delay audio record request")

                        if(
                            currentAudioRecordState is  RecordAudioState.Idle
                            && currentVideoRecordState is RecordVideoState.Idle
                        ) {
                            audioRecordServiceManager.startRecord()
                        } else {
                            Log.i(LOG_TAG,"Record alredy stated. Audio state:$currentAudioRecordState" +
                                    "Video state:$currentVideoRecordState")
                        }

                    }

                    DelayRecordTask.VIDEO_RECORD_TYPE -> {
                        if(
                            currentAudioRecordState is  RecordAudioState.Idle
                            && currentVideoRecordState is RecordVideoState.Idle
                        ) {
                            videoRecordServiceManager.startRecord()
                        } else {
                            Log.i(LOG_TAG,"Record alredy stated. Audio state:$currentAudioRecordState" +
                                    "Video state:$currentVideoRecordState")
                        }
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