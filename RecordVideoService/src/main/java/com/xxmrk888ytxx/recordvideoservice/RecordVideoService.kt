package com.xxmrk888ytxx.recordvideoservice

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication
import com.xxmrk888ytxx.recordvideoservice.models.ForegroundNotificationType
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import com.xxmrk888ytxx.videorecorder.VideoRecorder
import com.xxmrk888ytxx.videorecorder.models.RecorderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class RecordVideoService : Service(), RecordVideoServiceController, LifecycleOwner {

    //Notification Manager
    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService()!!
    }
    //

    //Service params
    private val recordVideoParams: RecordVideoParams by lazy {
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

    override fun onBind(intent: Intent?): IBinder {
        Log.i(LOG_TAG, "onBind")
        return LocalBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(LOG_TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.i(LOG_TAG, "onRebind")
        super.onRebind(intent)
    }

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
        Log.i(LOG_TAG, "onCreate")

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        registerReceiver(
            notificationCommandReceiver,
            IntentFilter(ServiceNotificationActions.VIDEO_RECORD_SERVICE_COMMAND_ACTION)
        )

        videoRecordServiceScope.launch(Dispatchers.Main) {
            applicationContext.buildNotificationChannel(
                id = NOTIFICATION_CHANNEL_ID,
                name = getString(R.string.Channel_name)
            )

            startForeground(NOTIFICATION_ID, createNotification())
            Log.i(LOG_TAG, "foreground started")
        }
    }
    //

    //Destroy service
    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        unregisterReceiver(notificationCommandReceiver)
        Log.i(LOG_TAG, "onDestroy")
        videoRecordServiceScope.launch(NonCancellable) {
            stopRecord()
            recordStateObserverScope.cancel()
            videoRecordServiceScope.cancel()
            notificationManager.cancel(NOTIFICATION_ID)
        }

    }
    //

    //Record control
    override fun startRecord(outputFile: File) {
        if (recorder != null) return
        videoRecordServiceScope.launch {

            val config = recordVideoParams.cameraConfig.first()

            recorder = VideoRecorder.Builder(
                applicationContext,
                outputFile,
                this@RecordVideoService
            ).apply {
                setCameraType(config.cameraType.cameraTypeForRecorder)
                setMaxQuality(config.maxQuality.videoRecorderMaxQuality)
                setCameraRotation(config.cameraRotation.cameraRotation)
            }.build()

            startRecordObserver()

            recorder?.run {
                startRecord()
            }
            Log.i(LOG_TAG, "startRecord")
        }
    }

    override fun pauseRecord() {
        if (recorder == null) return
        videoRecordServiceScope.launch {
            recorder?.run {
                pauseRecord()
            }

            Log.i(LOG_TAG, "pauseRecord")
        }
    }

    override fun resumeRecord() {
        if (recorder == null) return
        videoRecordServiceScope.launch {
            recorder?.run {
                resumeRecord()
            }

            Log.i(LOG_TAG, "resumeRecord")
        }
    }

    override fun stopRecord() {
        if (recorder == null) return
        videoRecordServiceScope.launch {
            stopRecordObserver()
            recorder?.run {
                stopRecordAndDestroyRecorder()
            }

            recorder = null

            Log.i(LOG_TAG, "stopRecord")

            withContext(NonCancellable) {
                recordVideoParams.saveRecordedVideoStrategy.saveRecord()
                Log.i(LOG_TAG, "record saved")
            }
        }
    }
    //

    //Record state observe
    private fun startRecordObserver() {
        recordStateObserverScope.cancelChillersAndLaunch {
            val foregroundType = recordVideoParams.cameraConfig.first().foregroundNotificationType
            recorder?.run {
                this.recordState.collect() { state ->
                    _currentState.update {
                        when (state) {
                            is RecorderState.Recording -> RecordVideoState.Recording(state.currentRecordDuration)

                            is RecorderState.Paused -> RecordVideoState.Pause(state.currentRecordDuration)

                            else -> RecordVideoState.Idle
                        }
                    }

                    if (isActive) {
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            createNotification()
                        )
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
    private suspend fun createNotification(): Notification {

        val foregroundType = recordVideoParams.cameraConfig.first().foregroundNotificationType

        return when (foregroundType) {
            is ForegroundNotificationType.CustomNotification -> {
                applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
                    setContentTitle(foregroundType.title)
                    setContentText(foregroundType.text)
                    setSmallIcon(R.drawable.baseline_videocam_24)
                    setOnlyAlertOnce(true)
                    if(foregroundType.isPauseResumeButtonActive) {
                        when(_currentState.value) {
                            is RecordVideoState.Recording -> {
                                addAction(ServiceNotificationActions.createActionForPauseRecord(
                                    context = applicationContext
                                ))
                            }
                            else -> addAction(ServiceNotificationActions.createActionForResumeRecord(
                                context = applicationContext
                            ))
                        }
                    }

                    if(foregroundType.isStopRecordButtonEnabled) {
                        addAction(
                            ServiceNotificationActions.createActionForStopRecord(
                                context = applicationContext
                            )
                        )
                    }
                }
            }

            is ForegroundNotificationType.ViewRecordStateType -> {
                createForegroundNotificationViewRecordStateType(_currentState.value)
            }
        }
    }

    private suspend fun createForegroundNotificationViewRecordStateType(recordVideoState: RecordVideoState): Notification {
        val foregroundType = recordVideoParams.cameraConfig.first().foregroundNotificationType
        return applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
            setContentTitle(
                when (recordVideoState) {
                    is RecordVideoState.Recording -> getString(R.string.Video_recording_is_on)
                    else -> getString(R.string.Video_recording_suspended)
                }
            )
            setContentText(
                "${getString(R.string.Recording_runs)} " +
                        recordVideoState.recordDuration.milliSecondToString()
            )
            setSmallIcon(R.drawable.baseline_videocam_24)
            setOnlyAlertOnce(true)
            if(foregroundType.isPauseResumeButtonActive) {
                when(_currentState.value) {
                    is RecordVideoState.Recording -> {
                        addAction(ServiceNotificationActions.createActionForPauseRecord(
                            context = applicationContext
                        ))
                    }
                    else -> addAction(ServiceNotificationActions.createActionForResumeRecord(
                        context = applicationContext
                    ))
                }
            }

            if(foregroundType.isStopRecordButtonEnabled) {
                addAction(
                    ServiceNotificationActions.createActionForStopRecord(
                        context = applicationContext
                    )
                )
            }
        }
    }
    //

    //Command Receiver
    private val notificationCommandReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent?.action != ServiceNotificationActions.VIDEO_RECORD_SERVICE_COMMAND_ACTION) return
                when(intent.getStringExtra(ServiceNotificationActions.COMMAND_KEY)) {

                    ServiceNotificationActions.STOP_RECORD_ACTION -> {
                        this@RecordVideoService.stopRecord()
                    }

                    ServiceNotificationActions.RESUME_RECORD_ACTION -> {
                        this@RecordVideoService.resumeRecord()
                    }

                    ServiceNotificationActions.PAUSE_RECORD_ACTION -> {
                        this@RecordVideoService.pauseRecord()
                    }
                }
            }

        }
    }
    //

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "VideoRecordServiceNotificationChannel"

        const val NOTIFICATION_ID = 2

        const val LOG_TAG = "RecordVideoService"
    }
}