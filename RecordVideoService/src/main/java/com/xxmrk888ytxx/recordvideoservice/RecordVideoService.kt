package com.xxmrk888ytxx.recordvideoservice

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
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
import com.xxmrk888ytxx.recordvideoservice.models.CameraType
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

private const val MIN_STORAGE_START_BYTES = 200 * 1024 * 1024L
private const val MIN_STORAGE_CRITICAL_BYTES = 100 * 1024 * 1024L

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

    private val stopRecordMutex = Mutex()

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationCommandReceiver)
        Log.i(LOG_TAG, "onDestroy")
        releaseWakeLock()
        abandonAudioFocus()
        videoRecordServiceScope.launch(NonCancellable) {
            try {
                stopRecordMutex.withLock {
                    stopRecordInternal()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
                }
                recordStateObserverScope.cancel()
                videoRecordServiceScope.cancel()
                recordNotificationStateObserverScope.cancel()

                notificationManager.cancel(NOTIFICATION_ID)
            }
        }
    }
    //

    private var currentPrimaryFile: File? = null
    private var currentSecondaryFile: File? = null
    private var currentCameraTypeOverride: CameraType? = null
    private var accumulatedDurationMs: Long = 0L
    private var isRotating: Boolean = false

    // WakeLock
    private val powerManager: PowerManager? by lazy {
        applicationContext.getSystemService()
    }
    private var wakeLock: PowerManager.WakeLock? = null

    private fun acquireWakeLock() {
        try {
            if (wakeLock == null) {
                wakeLock = powerManager?.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "BVAS:VideoRecordWakeLock"
                )?.apply {
                    setReferenceCounted(false)
                }
            }
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire(6 * 60 * 60 * 1000L) // 6 hours safety timeout
                Log.i(LOG_TAG, "WakeLock acquired")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to acquire WakeLock", e)
        }
    }

    private fun releaseWakeLock() {
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
                Log.i(LOG_TAG, "WakeLock released")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to release WakeLock", e)
        }
    }

    // Audio Focus & Call Handling
    private val audioManager: AudioManager? by lazy {
        applicationContext.getSystemService()
    }
    private var audioFocusRequest: AudioFocusRequest? = null
    private var wasRecordingBeforeFocusLoss = false

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.i(LOG_TAG, "Audio focus lost transiently (e.g. phone call), pausing video recording")
                if (_currentState.value is RecordVideoState.Recording) {
                    wasRecordingBeforeFocusLoss = true
                    pauseRecord()
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.i(LOG_TAG, "Audio focus regained")
                if (wasRecordingBeforeFocusLoss) {
                    wasRecordingBeforeFocusLoss = false
                    resumeRecord()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                Log.i(LOG_TAG, "Audio focus permanently lost, pausing video recording")
                wasRecordingBeforeFocusLoss = false
                pauseRecord()
            }
        }
    }

    private fun requestAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build()
                audioFocusRequest?.let { audioManager?.requestAudioFocus(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
                )
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to request audio focus", e)
        }
    }

    private fun abandonAudioFocus() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(audioFocusChangeListener)
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to abandon audio focus", e)
        }
    }

    // Storage Monitoring
    private fun getAvailableStorageBytes(): Long {
        val targetDir = currentPrimaryFile?.parentFile ?: applicationContext.filesDir
        return try {
            targetDir.usableSpace
        } catch (_: Exception) {
            Long.MAX_VALUE
        }
    }

    private fun showLowStorageNotification() {
        try {
            val channelId = getChannelId(com.xxmrk888ytxx.coreandroid.models.NotificationIconType.VIDEOCAM.id)
            ensureNotificationChannel(com.xxmrk888ytxx.coreandroid.models.NotificationIconType.VIDEOCAM.id)
            val notif = applicationContext.buildNotification(channelId) {
                setContentTitle(getString(R.string.video_recording_stopped))
                setContentText(getString(R.string.not_enough_free_space))
                setSmallIcon(android.R.drawable.stat_notify_error)
                setAutoCancel(true)
            }
            notificationManager.notify(NOTIFICATION_ID + 10, notif)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to show low storage notification", e)
        }
    }

    //Record control
    override fun startRecord(
        outputFile: File,
        secondaryOutputFile: File?,
        cameraTypeOverride: CameraType?
    ) {
        if (recorder != null) return

        val availableSpace = outputFile.parentFile?.usableSpace ?: applicationContext.filesDir.usableSpace
        if (availableSpace < MIN_STORAGE_START_BYTES) {
            Log.e(LOG_TAG, "Insufficient storage to start video recording: $availableSpace bytes available")
            showLowStorageNotification()
            return
        }

        currentPrimaryFile = outputFile
        currentSecondaryFile = secondaryOutputFile
        currentCameraTypeOverride = cameraTypeOverride
        accumulatedDurationMs = 0L
        isRotating = false

        acquireWakeLock()
        requestAudioFocus()

        videoRecordServiceScope.launch {
            val config = recordVideoParams.cameraConfig.first()
            val effectiveCameraType = currentCameraTypeOverride?.cameraTypeForRecorder ?: config.cameraType.cameraTypeForRecorder

            recorder = VideoRecorder.Builder(
                applicationContext,
                outputFile,
                this@RecordVideoService
            ).apply {
                setCameraType(effectiveCameraType)
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
        val oldRecorder = recorder
        recorder = null
        oldRecorder?.stopRecordAndDestroyRecorder()

        withContext(NonCancellable) {
            recordVideoParams.saveRecordedVideoStrategy.saveRecord()
            Log.i(LOG_TAG, "Previous chunk saved successfully")
        }

        val baseDir = currentPrimaryFile?.parentFile ?: applicationContext.cacheDir
        val nextPrimary = currentPrimaryFile ?: File(baseDir, "recording")
        val nextSecondary = currentSecondaryFile
        currentPrimaryFile = nextPrimary
        currentSecondaryFile = nextSecondary

        val config = recordVideoParams.cameraConfig.first()
        val effectiveCameraType = currentCameraTypeOverride?.cameraTypeForRecorder ?: config.cameraType.cameraTypeForRecorder
        recorder = VideoRecorder.Builder(
            applicationContext,
            nextPrimary,
            this@RecordVideoService
        ).apply {
            setCameraType(effectiveCameraType)
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
        releaseWakeLock()
        videoRecordServiceScope.launch {
            recorder?.run {
                pauseRecord()
            }
            Log.i(LOG_TAG, "pauseRecord")
        }
    }

    override fun resumeRecord() {
        if (recorder == null) return
        acquireWakeLock()
        videoRecordServiceScope.launch {
            recorder?.run {
                resumeRecord()
            }

            Log.i(LOG_TAG, "resumeRecord")
        }
    }

    override suspend fun stopRecord() {
        releaseWakeLock()
        abandonAudioFocus()
        stopRecordMutex.withLock {
            stopRecordInternal()
        }
    }

    private suspend fun stopRecordInternal() {
        val activeRecorder = recorder ?: return
        recorder = null
        stopRecordObserver(resetState = false)
        accumulatedDurationMs = 0L
        currentCameraTypeOverride = null
        isRotating = false

        try {
            activeRecorder.stopRecordAndDestroyRecorder()
            Log.i(LOG_TAG, "stopRecord")
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error stopping recorder", e)
        }

        try {
            withContext(NonCancellable) {
                recordVideoParams.saveRecordedVideoStrategy.saveRecord()
                Log.i(LOG_TAG, "record saved")
            }
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Error saving record", e)
        } finally {
            _currentState.update { RecordVideoState.Idle }
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

                        // Check low storage limit
                        val usableSpace = getAvailableStorageBytes()
                        if (usableSpace < MIN_STORAGE_CRITICAL_BYTES && !isRotating) {
                            Log.e(LOG_TAG, "Critical low storage (< 100MB)! Stopping video recording safely.")
                            showLowStorageNotification()
                            videoRecordServiceScope.launch {
                                stopRecord()
                            }
                            return@collect
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
                        videoRecordServiceScope.launch {
                            this@RecordVideoService.stopRecord()
                        }
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