package com.xxmrk888ytxx.videorecorder

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.util.Consumer
import androidx.lifecycle.LifecycleOwner
import com.xxmrk888ytxx.videorecorder.models.CameraRotation
import com.xxmrk888ytxx.videorecorder.models.CameraType
import com.xxmrk888ytxx.videorecorder.models.MaxQuality
import com.xxmrk888ytxx.videorecorder.models.RecorderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class VideoRecorderImpl constructor(
    private val context: Context,
    private val outputFile: File,
    private val lifecycleOwner: LifecycleOwner,
    private val scope:CoroutineScope,
    private val maxQuality: MaxQuality,
    private val cameraType: CameraType,
    private val cameraRotation: CameraRotation
) : VideoRecorder {

    private var recording: Recording? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    private val observerRecordDurationScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO.limitedParallelism(1)
    )

    //state
    private val _recordState:MutableStateFlow<RecorderState> = MutableStateFlow(RecorderState.Idle)

    override val recordState: Flow<RecorderState> = _recordState.asStateFlow()
    //

    private val listener by lazy {
        Consumer<VideoRecordEvent> { recordInfo ->
            observerRecordDurationScope.launch {
                if(recordInfo == null) return@launch
                val recordedMillis = recordInfo.recordingStats.recordedDurationNanos / 1_000_000
                _recordState.update {
                    when(it) {
                        is RecorderState.Recording -> RecorderState.Recording(recordedMillis)

                        is RecorderState.Paused -> RecorderState.Paused(recordedMillis)

                        else -> it
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun startRecord() {
        scope.launch {
            val qualitySelector = QualitySelector.fromOrderedList(
                maxQuality.qualityList,
                FallbackStrategy.lowerQualityOrHigherThan(Quality.SD)
            )

            val recorded = Recorder.Builder()
                .setQualitySelector(qualitySelector)
                .setExecutor(Dispatchers.IO.asExecutor())
                .build()

            videoCapture = VideoCapture.withOutput(recorded).apply {
                targetRotation = cameraRotation.rotation
            }

            cameraProvider = ProcessCameraProvider.getInstance(context).get().apply {
                withContext(Dispatchers.Main) {
                    bindToLifecycle(
                        lifecycleOwner,cameraType.cameraSelector,videoCapture
                    )
                }
            }

            val mediaStoreOutput = FileOutputOptions
                .Builder(outputFile)
                .build()

            toRecordState()

            recording = videoCapture!!.output
                .prepareRecording(context, mediaStoreOutput)
                .withAudioEnabled()
                .start(
                    Dispatchers.IO.asExecutor(),
                    listener
                )
        }
    }

    override fun pauseRecord() {
        scope.launch {
            recording?.run {
                pause()
            }

            toPauseState()
        }
    }

    override fun resumeRecord() {
        scope.launch {
            recording?.run {
                resume()
            }

            toRecordState()
        }
    }

    override fun stopRecordAndDestroyRecorder() {
        scope.launch {
            recording?.run {
                stop()
                close()
            }

            cameraProvider?.run {
                withContext(Dispatchers.Main) {
                    unbind(videoCapture)
                }
            }

            recording = null
            cameraProvider = null
            videoCapture = null

            toDestroyState()

            observerRecordDurationScope.cancel()
            scope.cancel()
        }
    }

    private suspend fun toRecordState() {
        _recordState.update { RecorderState.Recording(it.currentRecordDuration) }
    }

    private suspend fun toPauseState() {
        _recordState.update { RecorderState.Paused(it.currentRecordDuration) }
    }

    private suspend fun toDestroyState() {
        _recordState.update { RecorderState.Destroyed }
    }
}