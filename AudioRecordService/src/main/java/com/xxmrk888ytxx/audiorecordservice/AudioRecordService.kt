package com.xxmrk888ytxx.audiorecordservice

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.coreandroid.buildNotification
import com.xxmrk888ytxx.coreandroid.buildNotificationChannel
import com.xxmrk888ytxx.coreandroid.cancelChillersAndLaunch
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class AudioRecordService : Service(), AudioRecordServiceController {

    private val recordAudioParams:RecordAudioParams by lazy {
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
        Log.i(LOG_TAG,"onBind")
        return LocalBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(LOG_TAG,"onUnbind")
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        Log.i(LOG_TAG,"onRebind")
        super.onRebind(intent)
    }

    private val _currentState: MutableStateFlow<RecordAudioState> =
        MutableStateFlow(RecordAudioState.Idle)

    override fun onCreate() {
        super.onCreate()
        Log.i(LOG_TAG,"onCreate")
        applicationContext.buildNotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.Channel_name),
        )

        startForeground(NOTIFICATION_ID, foregroundNotification)

        Log.i(LOG_TAG,"foreground started")
    }

    override fun startRecord(recordFileOutput:File) {
        if (mediaRecorder != null) return
        audioRecordServiceScope.launch {
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                MediaRecorder(applicationContext)
            else MediaRecorder()

            mediaRecorder?.let { recorder ->
                recorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                    setOutputFile(recordFileOutput.absolutePath)
                    prepare()
                    start()
                }
            }

            toRecordingState()
            Log.i(LOG_TAG,"record started")
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
            Log.i(LOG_TAG,"record paused")
        }
    }

    override fun resumeRecord() {
        if (mediaRecorder == null) return
        audioRecordServiceScope.launch {
            mediaRecorder?.let { recorder ->
                recorder.apply {
                    resume()

                    toRecordingState()
                    Log.i(LOG_TAG,"record resumed")
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

            Log.i(LOG_TAG,"record stoped")
            withContext(NonCancellable) {
                recordAudioParams.saveAudioRecordStrategy.saveRecord()
                Log.i(LOG_TAG,"record saved")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_TAG,"onDestroy")
        audioRecordServiceScope.launch {
            stopRecord()
            durationObserverScope.cancel()
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

        const val LOG_TAG = "AudioRecordService"
    }
}