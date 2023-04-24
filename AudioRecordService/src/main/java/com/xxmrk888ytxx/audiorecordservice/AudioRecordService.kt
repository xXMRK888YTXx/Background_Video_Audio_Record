package com.xxmrk888ytxx.audiorecordservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

class AudioRecordService : Service(), AudioRecordServiceController {

    private var mediaRecorder: MediaRecorder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val audioRecordServiceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO.limitedParallelism(1)
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

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(applicationContext)
            else MediaRecorder()

            mediaRecorder?.let { recorder ->
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(File(applicationContext.cacheDir,"${System.currentTimeMillis()}.mp3").absolutePath)
                    prepare()
                    start()
                }
            }

            _currentState.emit(RecordAudioState.Recording(0))
        }
    }

    override fun pauseRecord() {
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    stop()
                }
            }

            _currentState.emit(RecordAudioState.Pause(0))
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

            _currentState.emit(RecordAudioState.Idle)
        }
    }


    override val currentState: Flow<RecordAudioState> = _currentState.asStateFlow()

    private val foregroundNotification: Notification
        get() = applicationContext.buildNotification(NOTIFICATION_CHANNEL_ID) {
            setContentTitle("Сервис записи аудио")
            setContentText("Запись в процессе")
        }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "AudioRecordServiceNotificationChannel"

        const val NOTIFICATION_ID = 1
    }
}