package com.xxmrk888ytxx.recordvideoservice

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val recordNotificationStateObserverScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    //Recorder
    private var recorder: VideoRecorder? = null
        get() = field
        set(value) {
            recordNotificationStateObserverScope.coroutineContext.cancelChildren()
            if (value != null) {
                recordNotificationStateObserverScope.launch {
                    value.recordState.onEach { delay(200) }.collect { updateNotification() }
                }
            }
            field = value
        }
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

    override val currentState: Flow<RecordVideoState> = _currentState.asStateFlow().onEach {
        Log.d("1", it.toString())
    }

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
            IntentFilter(ServiceNotificationActions.VIDEO_RECORD_SERVICE_COMMAND_ACTION),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) RECEIVER_EXPORTED else 0
        )

        videoRecordServiceScope.launch(Dispatchers.Main) {
            applicationContext.buildNotificationChannel(
                id = NOTIFICATION_CHANNEL_ID,
                name = getString(R.string.Channel_name)
            )

            val notification = createNotification()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                var serviceType = android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    serviceType = serviceType or android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                }
                startForeground(NOTIFICATION_ID, notification, serviceType)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
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
            recordNotificationStateObserverScope.cancel()

            notificationManager.cancel(NOTIFICATION_ID)
        }

    }
    //

    private var currentPrimaryFile: File? = null
    private var currentSecondaryFile: File? = null
    private var accumulatedDurationMs: Long = 0L
    private var isRotating: Boolean = false

    //Record control
    override fun startRecord(outputFile: File, secondaryOutputFile: File?) {
        if (recorder != null) return
        currentPrimaryFile = outputFile
        currentSecondaryFile = secondaryOutputFile
        accumulatedDurationMs = 0L
        isRotating = false

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
                if (secondaryOutputFile != null) {
                    setSecondaryOutputFile(secondaryOutputFile)
                }
            }.build()

            startRecordObserver()

            recorder?.run {
                startRecord()
            }
            Log.i(LOG_TAG, "startRecord")
        }
    }

    private suspend fun rotateChunk(lastChunkDuration: Long) {
        Log.i(LOG_TAG, "Rotating video recording chunk at duration $lastChunkDuration ms")
        accumulatedDurationMs += lastChunkDuration

        stopRecordObserver(resetState = false)
        recorder?.run {
            stopRecordAndDestroyRecorder()
        }
        recorder = null

        withContext(NonCancellable) {
            recordVideoParams.saveRecordedVideoStrategy.saveRecord()
            Log.i(LOG_TAG, "Previous chunk saved successfully")
        }

        val baseDir = currentPrimaryFile?.parentFile ?: applicationContext.cacheDir
        val timestamp = System.currentTimeMillis()
        val nextPrimary = File(baseDir, "REC_${timestamp}.mp4")
        val nextSecondary = if (currentSecondaryFile != null) File(baseDir, "REC_${timestamp}_secondary.mp4") else null
        currentPrimaryFile = nextPrimary
        currentSecondaryFile = nextSecondary

        val config = recordVideoParams.cameraConfig.first()
        recorder = VideoRecorder.Builder(
            applicationContext,
            nextPrimary,
            this@RecordVideoService
        ).apply {
            setCameraType(config.cameraType.cameraTypeForRecorder)
            setMaxQuality(config.maxQuality.videoRecorderMaxQuality)
            setCameraRotation(config.cameraRotation.cameraRotation)
            if (nextSecondary != null) {
                setSecondaryOutputFile(nextSecondary)
            }
        }.build()

        startRecordObserver()
        recorder?.run {
            startRecord()
        }
        isRotating = false
        Log.i(LOG_TAG, "Next chunk recording started")
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
            stopRecordObserver(resetState = true)
            recorder?.run {
                stopRecordAndDestroyRecorder()
            }

            recorder = null
            accumulatedDurationMs = 0L
            isRotating = false

            Log.i(LOG_TAG, "stopRecord")

            withContext(NonCancellable) {
                recordVideoParams.saveRecordedVideoStrategy.saveRecord()
                Log.i(LOG_TAG, "record saved")
            }
        }
    }
    //

    //Record state observe
    @OptIn(FlowPreview::class)
    private fun startRecordObserver() {
        recordStateObserverScope.cancelChillersAndLaunch {
            recorder?.run {
                recordState
                    .collect() { state ->
                        val chunkDuration = when (state) {
                            is RecorderState.Recording -> state.currentRecordDuration
                            is RecorderState.Paused -> state.currentRecordDuration
                            else -> 0L
                        }
                        val totalDuration = accumulatedDurationMs + chunkDuration
                        _currentState.update {
                            when (state) {
                                is RecorderState.Recording -> RecordVideoState.Recording(totalDuration)
                                is RecorderState.Paused -> RecordVideoState.Pause(totalDuration)
                                else -> RecordVideoState.Idle
                            }
                        }

                        // Check video chunk split limit
                        val config = recordVideoParams.cameraConfig.first()
                        val splitLimit = com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit.fromId(config.videoSplitLimitId)
                        val shouldRotate = splitLimit != com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit.DISABLED &&
                                chunkDuration >= splitLimit.maxDurationMs &&
                                !isRotating

                        if (shouldRotate) {
                            isRotating = true
                            videoRecordServiceScope.launch {
                                rotateChunk(chunkDuration)
                            }
                        }
                    }
            }
        }
    }

    private fun stopRecordObserver(resetState: Boolean = true) {
        recordStateObserverScope.coroutineContext.cancelChildren()
        if (resetState) {
            _currentState.update { RecordVideoState.Idle }
        }
    }
    //

    private fun getChannelId(iconId: Int): String = "${NOTIFICATION_CHANNEL_ID}_$iconId"

    private fun ensureNotificationChannel(iconId: Int) {
        applicationContext.buildNotificationChannel(
            id = getChannelId(iconId),
            name = getString(R.string.Channel_name)
        )
    }

    //Foreground notification
    private suspend fun createNotification(): Notification {

        val foregroundType = recordVideoParams.cameraConfig.first().foregroundNotificationType
        val channelId = getChannelId(foregroundType.iconId)
        ensureNotificationChannel(foregroundType.iconId)

        return when (foregroundType) {
            is ForegroundNotificationType.CustomNotification -> {
                applicationContext.buildNotification(channelId) {
                    setContentTitle(foregroundType.title)
                    setContentText(foregroundType.text)
                    val iconRes = com.xxmrk888ytxx.coreandroid.models.NotificationIconType.fromId(foregroundType.iconId).iconRes
                    com.xxmrk888ytxx.coreandroid.NotificationHelper.applyNotificationIcon(this, applicationContext, iconRes)
                    setOnlyAlertOnce(true)
                    if (foregroundType.isPauseResumeButtonActive) {
                        when (_currentState.value) {
                            is RecordVideoState.Recording -> {
                                addAction(
                                    ServiceNotificationActions.createActionForPauseRecord(
                                        context = applicationContext
                                    )
                                )
                            }

                            else -> addAction(
                                ServiceNotificationActions.createActionForResumeRecord(
                                    context = applicationContext
                                )
                            )
                        }
                    }

                    if (foregroundType.isStopRecordButtonEnabled) {
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
        val channelId = getChannelId(foregroundType.iconId)
        ensureNotificationChannel(foregroundType.iconId)

        return applicationContext.buildNotification(channelId) {
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
            val iconRes = com.xxmrk888ytxx.coreandroid.models.NotificationIconType.fromId(foregroundType.iconId).iconRes
            com.xxmrk888ytxx.coreandroid.NotificationHelper.applyNotificationIcon(this, applicationContext, iconRes)
            setOnlyAlertOnce(true)
            if (foregroundType.isPauseResumeButtonActive) {
                when (_currentState.value) {
                    is RecordVideoState.Recording -> {
                        addAction(
                            ServiceNotificationActions.createActionForPauseRecord(
                                context = applicationContext
                            )
                        )
                    }

                    else -> addAction(
                        ServiceNotificationActions.createActionForResumeRecord(
                            context = applicationContext
                        )
                    )
                }
            }

            if (foregroundType.isStopRecordButtonEnabled) {
                addAction(
                    ServiceNotificationActions.createActionForStopRecord(
                        context = applicationContext
                    )
                )
            }
        }
    }

    private val mutex = Mutex()

    private suspend fun updateNotification() = mutex.withLock {
        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification()
        )
    }
    //

    //Command Receiver
    private val notificationCommandReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("1", "onReceive ${intent.toString()}")
                if (intent?.action != ServiceNotificationActions.VIDEO_RECORD_SERVICE_COMMAND_ACTION) return
                when (intent.getStringExtra(ServiceNotificationActions.COMMAND_KEY)) {

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