package com.xxmrk888ytxx.audiorecordservice

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AudioRecordService : Service(), AudioRecordServiceController {

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

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    private val _currentState: MutableStateFlow<RecordAudioState> =
        MutableStateFlow(RecordAudioState.Idle)

    override fun onCreate() {
        super.onCreate()
        applicationContext.buildNotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.Channel_name),
        )

        startForeground(NOTIFICATION_ID, foregroundNotification)
    }

    override fun startRecord() {
        audioRecordServiceScope.launch {
            if (mediaRecorder != null) return@launch

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                MediaRecorder(applicationContext)
            else MediaRecorder()

            mediaRecorder?.let { recorder ->
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(
                        File(
                            applicationContext.cacheDir,
                            "${System.currentTimeMillis()}.mp3"
                        ).absolutePath
                    )
                    prepare()
                    start()
                }
            }

            toRecordingState()
        }
    }

    override fun pauseRecord() {
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    pause()
                }
            }

            toPauseState()
        }
    }

    override fun resumeRecord() {
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    resume()

                    toRecordingState()
                }
            }
        }
    }

    override fun stopRecord() {
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    stop()
                    release()
                    mediaRecorder = null
                }
            }

            toIdleState()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecordServiceScope.launch {
            stopRecord()
            audioRecordServiceScope.cancel()
        }
    }

    override val currentState: Flow<RecordAudioState> = _currentState.asStateFlow()

    private val foregroundNotification: Notification
        get() = applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
            setContentTitle("Сервис записи аудио")
            setContentText("Запись в процессе")
        }

    private suspend fun toIdleState() {
        _currentState.update { RecordAudioState.Idle }
        stopDurationObserver()
    }

    private suspend fun toPauseState() {
        _currentState.update { RecordAudioState.Pause(it.recordDuration) }
        stopDurationObserver()
    }

    private suspend fun toRecordingState() {
        _currentState.update { RecordAudioState.Recording(it.recordDuration) }
        startDurationObserver()
    }

    private fun startDurationObserver() {
        durationObserverScope.cancelChillersAndLaunch {
            while (true) {
                if (_currentState.value is RecordAudioState.Recording) {
                    _currentState.update {
                        when (it) {
                            is RecordAudioState.Recording ->
                                it.copy(it.recordDuration + 1000)

                            else -> it
                        }
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
    }
}