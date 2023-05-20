package com.xxmrk888ytxx.audiorecordservice

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.EncoderProfiles
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
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

class AudioRecordService : Service(), AudioRecordServiceController {

    private val notificationManager: NotificationManager by lazy {
        applicationContext.getSystemService()!!
    }

    private val recordAudioParams: RecordAudioParams by lazy {
        applicationContext.getDepsByApplication()
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

            Log.i(LOG_TAG, "foreground started")
        }
    }

    override fun startRecord(recordFileOutput: File) {
        if (mediaRecorder != null) return
        audioRecordServiceScope.launch {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                MediaRecorder(applicationContext)
            else MediaRecorder()

            mediaRecorder?.let { recorder ->
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setAudioSamplingRate(44100)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setOutputFile(recordFileOutput.absolutePath)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
                    setAudioEncodingBitRate(16)
                    prepare()
                    start()
                }
            }

            toRecordingState()
            Log.i(LOG_TAG, "record started")
        }
    }

    override fun pauseRecord() {
        if (mediaRecorder == null) return
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
        audioRecordServiceScope.launch {
            stopRecord()
            durationObserverScope.cancel()
            audioRecordServiceScope.cancel()
        }
    }

    override val currentState: Flow<RecordAudioState> = _currentState.asStateFlow()

    private suspend fun createNotification(): Notification {
        val foregroundType = recordAudioParams.recordAudioConfig.first().foregroundNotificationType

        return when (foregroundType) {
            is ForegroundNotificationType.CustomNotification -> {
                applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
                    setContentTitle(foregroundType.title)
                    setContentText(foregroundType.text)
                    setSmallIcon(R.drawable.record)
                }
            }

            is ForegroundNotificationType.ViewRecordStateType -> {
                createForegroundNotificationViewRecordStateType(_currentState.value)
            }
        }
    }

    private fun createForegroundNotificationViewRecordStateType(recordAudioState: RecordAudioState): Notification {
        return applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
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
            setSmallIcon(R.drawable.record)
            setOnlyAlertOnce(true)
        }
    }

    private suspend fun toIdleState() {
        _currentState.update { RecordAudioState.Idle }
        stopDurationObserver()
    }

    private suspend fun toPauseState() {
        _currentState.update { RecordAudioState.Pause(it.recordDuration) }
        stopDurationObserver()

        if (recordAudioParams.recordAudioConfig.first().foregroundNotificationType is ForegroundNotificationType.ViewRecordStateType) {
            notificationManager.notify(
                NOTIFICATION_ID,
                createForegroundNotificationViewRecordStateType(_currentState.value)
            )
        }
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
                    _currentState.update {
                        when (it) {
                            is RecordAudioState.Recording ->
                                it.copy(it.recordDuration + 1000)

                            else -> it
                        }
                    }

                    if (foregroundNotificationType is ForegroundNotificationType.ViewRecordStateType && isActive) {
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            createForegroundNotificationViewRecordStateType(_currentState.value)
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
    }
}