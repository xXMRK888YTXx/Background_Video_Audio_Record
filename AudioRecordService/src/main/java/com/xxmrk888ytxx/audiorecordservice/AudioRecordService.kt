package com.xxmrk888ytxx.audiorecordservice

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
import android.media.EncoderProfiles
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import com.xxmrk888ytxx.audiorecordservice.models.ForegroundNotificationType
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.coreandroid.toTimeString
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val MIN_STORAGE_START_BYTES = 50 * 1024 * 1024L
private const val MIN_STORAGE_CRITICAL_BYTES = 20 * 1024 * 1024L

class AudioRecordService : Service(), AudioRecordServiceController {

    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService()!!
    }

    private val recordAudioParams: RecordAudioParams by lazy {
        applicationContext.getDepsByApplication()
    }

    private val notificationCommandReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action != ServiceNotificationActions.AUDIO_RECORD_SERVICE_COMMAND_ACTION) return
                when (intent.getStringExtra(ServiceNotificationActions.COMMAND_KEY)) {

                    ServiceNotificationActions.STOP_RECORD_ACTION -> {
                        this@AudioRecordService.stopRecord()
                    }

                    ServiceNotificationActions.RESUME_RECORD_ACTION -> {
                        this@AudioRecordService.resumeRecord()
                    }

                    ServiceNotificationActions.PAUSE_RECORD_ACTION -> {
                        this@AudioRecordService.pauseRecord()
                    }
                }
            }

        }
    }

    private var mediaRecorder: MediaRecorder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val audioRecordServiceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default.limitedParallelism(1)
    )

    private val durationObserverScope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default
    )

    inner class LocalBinder : Binder() {
        val controller: AudioRecordServiceController = this@AudioRecordService
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

    private val _currentState: MutableStateFlow<RecordAudioState> =
        MutableStateFlow(RecordAudioState.Idle)

    override fun onCreate() {
        super.onCreate()
        audioRecordServiceScope.launch(Dispatchers.Main) {
            Log.i(LOG_TAG, "onCreate")
            applicationContext.buildNotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.Channel_name),
            )

            startForeground(NOTIFICATION_ID, createNotification())

            registerReceiver(
                notificationCommandReceiver, IntentFilter(
                    ServiceNotificationActions.AUDIO_RECORD_SERVICE_COMMAND_ACTION
                ),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) RECEIVER_EXPORTED else 0
            )

            Log.i(LOG_TAG, "foreground started")
        }
    }

    private var currentOutputFile: File? = null

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
                    "BVAS:AudioRecordWakeLock"
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
                Log.i(LOG_TAG, "Audio focus lost transiently (e.g. phone call), pausing audio recording")
                if (_currentState.value is RecordAudioState.Recording) {
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
                Log.i(LOG_TAG, "Audio focus permanently lost, pausing audio recording")
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
        val targetDir = currentOutputFile?.parentFile ?: applicationContext.filesDir
        return try {
            targetDir.usableSpace
        } catch (_: Exception) {
            Long.MAX_VALUE
        }
    }

    private fun showLowStorageNotification() {
        try {
            val channelId = getChannelId(com.xxmrk888ytxx.coreandroid.models.NotificationIconType.MIC.id)
            ensureNotificationChannel(com.xxmrk888ytxx.coreandroid.models.NotificationIconType.MIC.id)
            val notif = applicationContext.buildNotification(channelId) {
                setContentTitle(getString(R.string.audio_recording_stopped))
                setContentText(getString(R.string.not_enough_free_space))
                setSmallIcon(android.R.drawable.stat_notify_error)
                setAutoCancel(true)
            }
            notificationManager.notify(NOTIFICATION_ID + 10, notif)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to show low storage notification", e)
        }
    }

    override fun startRecord(recordFileOutput: File) {
        if (mediaRecorder != null) return

        val availableSpace = recordFileOutput.parentFile?.usableSpace ?: applicationContext.filesDir.usableSpace
        if (availableSpace < MIN_STORAGE_START_BYTES) {
            Log.e(LOG_TAG, "Insufficient storage to start audio recording: $availableSpace bytes available")
            showLowStorageNotification()
            return
        }

        currentOutputFile = recordFileOutput
        acquireWakeLock()
        requestAudioFocus()

        audioRecordServiceScope.launch {
            fun createRecorder(): MediaRecorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    MediaRecorder(applicationContext)
                else MediaRecorder()

            var recorder = createRecorder()
            var prepared = false

            // Try 1: Studio Quality Stereo AAC (48kHz, 256kbps, 2 channels)
            try {
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setOutputFile(recordFileOutput.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setAudioChannels(2)
                    setAudioSamplingRate(48000)
                    setAudioEncodingBitRate(256000)
                    prepare()
                    prepared = true
                }
            } catch (e: Exception) {
                Log.w(LOG_TAG, "Stereo 48kHz AAC recording unsupported, trying high-quality Mono 48kHz AAC", e)
                try {
                    recorder.reset()
                    recorder.release()
                } catch (_: Exception) {}
            }

            // Try 2: High Quality Mono AAC (48kHz, 192kbps, 1 channel)
            if (!prepared) {
                recorder = createRecorder()
                try {
                    recorder.apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setOutputFile(recordFileOutput.absolutePath)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioChannels(1)
                        setAudioSamplingRate(48000)
                        setAudioEncodingBitRate(192000)
                        prepare()
                        prepared = true
                    }
                } catch (e: Exception) {
                    Log.w(LOG_TAG, "48kHz Mono AAC failed, falling back to standard 44.1kHz AAC", e)
                    try {
                        recorder.reset()
                        recorder.release()
                    } catch (_: Exception) {}
                }
            }

            // Try 3: Standard 44.1kHz AAC (128kbps, 1 channel)
            if (!prepared) {
                recorder = createRecorder()
                try {
                    recorder.apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setOutputFile(recordFileOutput.absolutePath)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioSamplingRate(44100)
                        setAudioEncodingBitRate(128000)
                        prepare()
                        prepared = true
                    }
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to initialize high quality audio recorder", e)
                }
            }

            if (prepared) {
                try {
                    recorder.start()
                    mediaRecorder = recorder
                    toRecordingState()
                    Log.i(LOG_TAG, "High quality audio recording started")
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Failed to start MediaRecorder", e)
                    try {
                        recorder.release()
                    } catch (_: Exception) {}
                    mediaRecorder = null
                    toIdleState()
                }
            } else {
                mediaRecorder = null
                toIdleState()
            }
        }
    }

    override fun pauseRecord() {
        if (mediaRecorder == null) return
        releaseWakeLock()
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    pause()
                }
            }

            toPauseState()
            Log.i(LOG_TAG, "record paused")
        }
    }

    override fun resumeRecord() {
        if (mediaRecorder == null) return
        acquireWakeLock()
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    resume()

                    toRecordingState()
                    Log.i(LOG_TAG, "record resumed")
                }
            }
        }
    }

    override fun stopRecord() {
        if (mediaRecorder == null) return
        releaseWakeLock()
        abandonAudioFocus()
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    stop()
                    release()
                    mediaRecorder = null
                }
            }


            toIdleState()

            Log.i(LOG_TAG, "record stoped")
            withContext(NonCancellable) {
                recordAudioParams.saveAudioRecordStrategy.saveRecord()
                Log.i(LOG_TAG, "record saved")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG, "onDestroy")
        releaseWakeLock()
        abandonAudioFocus()
        unregisterReceiver(notificationCommandReceiver)
        audioRecordServiceScope.launch(NonCancellable) {
            stopRecord()
            durationObserverScope.cancel()
            audioRecordServiceScope.cancel()
            notificationManager.cancel(NOTIFICATION_ID)
        }
    }

    override val currentState: Flow<RecordAudioState> = _currentState.asStateFlow()

    private fun getChannelId(iconId: Int): String = "${NOTIFICATION_CHANNEL_ID}_$iconId"

    private fun ensureNotificationChannel(iconId: Int) {
        applicationContext.buildNotificationChannel(
            id = getChannelId(iconId),
            name = getString(R.string.Channel_name)
        )
    }

    private suspend fun createNotification(): Notification {
        val foregroundType = recordAudioParams.recordAudioConfig.first().foregroundNotificationType
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
                            is RecordAudioState.Recording -> {
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

    private suspend fun createForegroundNotificationViewRecordStateType(recordAudioState: RecordAudioState): Notification {
        val config = recordAudioParams.recordAudioConfig.first().foregroundNotificationType
        val channelId = getChannelId(config.iconId)
        ensureNotificationChannel(config.iconId)

        return applicationContext.buildNotification(channelId) {
            setContentTitle(
                when (recordAudioState) {
                    is RecordAudioState.Recording -> getString(R.string.Sound_recording_in_progress)
                    else -> getString(R.string.Recording_suspended)
                }
            )
            setContentText(
                "${getString(R.string.Recording_is_going_on)} " +
                        recordAudioState.recordDuration.milliSecondToString()
            )
            val iconRes = com.xxmrk888ytxx.coreandroid.models.NotificationIconType.fromId(config.iconId).iconRes
            com.xxmrk888ytxx.coreandroid.NotificationHelper.applyNotificationIcon(this, applicationContext, iconRes)
            setOnlyAlertOnce(true)
            if (config.isPauseResumeButtonActive) {
                when (_currentState.value) {
                    is RecordAudioState.Recording -> {
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

            if (config.isStopRecordButtonEnabled) {
                addAction(
                    ServiceNotificationActions.createActionForStopRecord(
                        context = applicationContext
                    )
                )
            }
        }
    }

    private suspend fun toIdleState() {
        _currentState.update { RecordAudioState.Idle }
        stopDurationObserver()
    }

    private suspend fun toPauseState() {
        _currentState.update { RecordAudioState.Pause(it.recordDuration) }
        stopDurationObserver()

        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification()
        )
    }

    private suspend fun toRecordingState() {
        _currentState.update { RecordAudioState.Recording(it.recordDuration) }
        startDurationObserver()
    }

    private fun startDurationObserver() {
        durationObserverScope.cancelChillersAndLaunch {
            val foregroundNotificationType =
                recordAudioParams.recordAudioConfig.first().foregroundNotificationType

            while (isActive) {
                if (_currentState.value is RecordAudioState.Recording) {
                    val usableSpace = getAvailableStorageBytes()
                    if (usableSpace < MIN_STORAGE_CRITICAL_BYTES) {
                        Log.e(LOG_TAG, "Critical low storage (< 20MB)! Stopping audio recording safely.")
                        showLowStorageNotification()
                        stopRecord()
                        return@cancelChillersAndLaunch
                    }

                    _currentState.update {
                        when (it) {
                            is RecordAudioState.Recording ->
                                it.copy(it.recordDuration + 1000)

                            else -> it
                        }
                    }

                    if (isActive) {
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            createNotification()
                        )
                    }
                }

                delay(1000)
            }
        }
    }

    private fun stopDurationObserver() {
        durationObserverScope.coroutineContext.cancelChildren()
    }


    companion object {
        const val NOTIFICATION_CHANNEL_ID = "AudioRecordServiceNotificationChannel"

        const val NOTIFICATION_ID = 1

        const val LOG_TAG = "AudioRecordService"

        const val MIN_STORAGE_START_BYTES = 50 * 1024 * 1024L

        const val MIN_STORAGE_CRITICAL_BYTES = 20 * 1024 * 1024L
    }
}