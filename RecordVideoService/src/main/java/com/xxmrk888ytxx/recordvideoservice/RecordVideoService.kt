package com.xxmrk888ytxx.recordvideoservice

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import com.xxmrk888ytxx.videorecorder.VideoRecorder
import com.xxmrk888ytxx.videorecorder.models.RecorderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RecordVideoService : Service(), RecordVideoServiceController, LifecycleOwner {

    //Service params
    private val recordVideoParams:RecordVideoParams by lazy {
        applicationContext.getDepsByApplication()
    }
    //

    //Scopes
    @OptIn(ExperimentalCoroutinesApi::class)
    private val videoRecordServiceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default.limitedParallelism(1)
    )

    private val recordStateObserverScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )
    //

    //Recorder
    private var recorder: VideoRecorder? = null
    //


    //Binding
    inner class LocalBinder : Binder() {
        val controller: RecordVideoServiceController = this@RecordVideoService
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()
    //

    //State
    private val _currentState: MutableStateFlow<RecordVideoState> =
        MutableStateFlow(RecordVideoState.Idle)

    override val currentState: Flow<RecordVideoState> = _currentState.asStateFlow()
    //

    //Lifecycle

    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle = lifecycleRegistry
    //

    //Start service
    override fun onCreate() {
        super.onCreate()
        applicationContext.buildNotificationChannel(
            id = NOTIFICATION_CHANNEL_ID,
            name = getString(R.string.Channel_name)
        )

        startForeground(NOTIFICATION_ID, foregroundNotification)

        videoRecordServiceScope.launch {
            withContext(Dispatchers.Main) {
                lifecycleRegistry.currentState = Lifecycle.State.RESUMED
            }
        }
    }
    //

    //Destroy service
    override fun onDestroy() {
        super.onDestroy()
        videoRecordServiceScope.launch {
            stopRecord()
            withContext(Dispatchers.Main) {
                lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            }
            recordStateObserverScope.cancel()
            videoRecordServiceScope.cancel()
        }

    }
    //

    //Record control
    override fun startRecord(outputFile: File) {
        videoRecordServiceScope.launch {
            if(recorder != null) return@launch

            recorder = VideoRecorder.Builder(
                applicationContext,
                outputFile,
                this@RecordVideoService
            ).apply {

            }.build()

            startRecordObserver()

            recorder?.run {
                startRecord()
            }
        }
    }

    override fun pauseRecord() {
        videoRecordServiceScope.launch {
            recorder?.run {
                pauseRecord()
            }
        }
    }

    override fun resumeRecord() {
        videoRecordServiceScope.launch {
            recorder?.run {
                resumeRecord()
            }
        }
    }

    override fun stopRecord() {
        videoRecordServiceScope.launch {
            stopRecordObserver()
            recorder?.run {
                stopRecordAndDestroyRecorder()
            }

            recorder = null

            recordVideoParams.saveRecordedVideoStrategy.saveRecord()
        }
    }
    //

    //Record state observe
    private fun startRecordObserver() {
        recordStateObserverScope.cancelChillersAndLaunch {
            recorder?.run {
                this.recordState.collect() { state ->
                    _currentState.update {
                        when (state) {
                            is RecorderState.Recording -> RecordVideoState.Recording(state.currentRecordDuration)

                            is RecorderState.Paused -> RecordVideoState.Pause(state.currentRecordDuration)

                            else -> RecordVideoState.Idle
                        }
                    }
                }
            }
        }
    }

    private fun stopRecordObserver() {
        recordStateObserverScope.coroutineContext.cancelChildren()
        _currentState.update { RecordVideoState.Idle }
    }
    //

    //Foreground notification
    private val foregroundNotification: Notification
        get() = applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
            setContentTitle("Сервис записи видео")
            setContentText("Запись в процессе")
        }
    //

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "VideoRecordServiceNotificationChannel"

        const val NOTIFICATION_ID = 2
    }
}