package com.xxmrk888ytxx.videorecorder

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.WindowManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.CompositionSettings
import androidx.camera.core.ConcurrentCamera
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import javax.inject.Inject

internal class VideoRecorderImpl constructor(
    private val context: Context,
    private val outputFile: File,
    private val secondaryOutputFile: File? = null,
    private val lifecycleOwner: LifecycleOwner,
    private val scope:CoroutineScope,
    private val maxQuality: MaxQuality,
    private val cameraType: CameraType,
    private val cameraRotation: CameraRotation
) : VideoRecorder {

    private var recording: Recording? = null
    private var secondaryRecording: Recording? = null
    private var primaryFinalizeDeferred: CompletableDeferred<Unit>? = null
    private var secondaryFinalizeDeferred: CompletableDeferred<Unit>? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var secondaryVideoCapture: VideoCapture<Recorder>? = null

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
            if (recordInfo == null) return@Consumer
            if (recordInfo is VideoRecordEvent.Finalize) {
                if (recordInfo.hasError()) {
                    Log.e("VideoRecorder", "Recording finalized with error: ${recordInfo.error}, cause: ${recordInfo.cause}")
                    observerRecordDurationScope.launch {
                        _recordState.update { RecorderState.Idle }
                    }
                }
                primaryFinalizeDeferred?.complete(Unit)
                return@Consumer
            }
            observerRecordDurationScope.launch {
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

            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            @Suppress("DEPRECATION")
            val displayRotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    context.display?.rotation ?: windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
                } catch (_: Exception) {
                    windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
                }
            } else {
                windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
            }

            val effectiveRotation = if (cameraRotation.rotation != Surface.ROTATION_0) {
                cameraRotation.rotation
            } else {
                displayRotation
            }

            val isLandscape = (effectiveRotation == Surface.ROTATION_90 || effectiveRotation == Surface.ROTATION_270)
            val aspectRatio = if (isLandscape) Rational(16, 9) else Rational(9, 16)
            val viewPort = ViewPort.Builder(aspectRatio, effectiveRotation)
                .setScaleType(ViewPort.FILL_CENTER)
                .build()

            val recorded = Recorder.Builder()
                .setQualitySelector(qualitySelector)
                .setExecutor(Dispatchers.IO.asExecutor())
                .build()

            videoCapture = VideoCapture.withOutput(recorded).apply {
                targetRotation = effectiveRotation
            }

            cameraProvider = ProcessCameraProvider.getInstance(context).get().apply {
                withContext(Dispatchers.Main) {
                    try {
                        unbindAll()
                    } catch (_: Exception) {}

                    if (cameraType is CameraType.Both) {
                        val concurrentCameras = availableConcurrentCameraInfos
                        if (concurrentCameras.isNotEmpty()) {
                            var primarySelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            var secondarySelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            for (cameraInfoList in concurrentCameras) {
                                for (cameraInfo in cameraInfoList) {
                                    if (cameraInfo.lensFacing == CameraSelector.LENS_FACING_BACK) {
                                        primarySelector = cameraInfo.cameraSelector
                                    } else if (cameraInfo.lensFacing == CameraSelector.LENS_FACING_FRONT) {
                                        secondarySelector = cameraInfo.cameraSelector
                                    }
                                }
                            }

                            if (secondaryOutputFile != null) {
                                // Independent dual-camera recording
                                val secondaryRecorder = Recorder.Builder()
                                    .setQualitySelector(qualitySelector)
                                    .setExecutor(Dispatchers.IO.asExecutor())
                                    .build()
                                secondaryVideoCapture = VideoCapture.withOutput(secondaryRecorder).apply {
                                    targetRotation = effectiveRotation
                                }

                                val primaryGroup = UseCaseGroup.Builder()
                                    .setViewPort(viewPort)
                                    .addUseCase(videoCapture!!)
                                    .build()
                                val secondaryGroup = UseCaseGroup.Builder()
                                    .setViewPort(viewPort)
                                    .addUseCase(secondaryVideoCapture!!)
                                    .build()

                                val primaryConfig = ConcurrentCamera.SingleCameraConfig(
                                    primarySelector,
                                    primaryGroup,
                                    lifecycleOwner
                                )
                                val secondaryConfig = ConcurrentCamera.SingleCameraConfig(
                                    secondarySelector,
                                    secondaryGroup,
                                    lifecycleOwner
                                )
                                try {
                                    bindToLifecycle(listOf(primaryConfig, secondaryConfig))
                                } catch (e: Exception) {
                                    Log.w("VideoRecorder", "Dual camera independent binding failed, fallback to back camera", e)
                                    try {
                                        val fallbackGroup = UseCaseGroup.Builder()
                                            .setViewPort(viewPort)
                                            .addUseCase(videoCapture!!)
                                            .build()
                                        bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, fallbackGroup)
                                    } catch (_: Exception) {
                                        bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, videoCapture!!)
                                    }
                                    secondaryVideoCapture = null
                                }
                            } else {
                                // Hardware Real-Time Dual Composition
                                val dummyPreview = Preview.Builder().build()
                                val useCaseGroup = UseCaseGroup.Builder()
                                    .setViewPort(viewPort)
                                    .addUseCase(videoCapture!!)
                                    .addUseCase(dummyPreview)
                                    .build()
                                val primaryConfig = ConcurrentCamera.SingleCameraConfig(
                                    primarySelector,
                                    useCaseGroup,
                                    CompositionSettings.Builder()
                                        .setAlpha(1.0f)
                                        .setOffset(0.0f, 0.0f)
                                        .setScale(1.0f, 1.0f)
                                        .build(),
                                    lifecycleOwner
                                )
                                val secondaryConfig = ConcurrentCamera.SingleCameraConfig(
                                    secondarySelector,
                                    useCaseGroup,
                                    CompositionSettings.Builder()
                                        .setAlpha(1.0f)
                                        .setOffset(0.6f, 0.6f)
                                        .setScale(0.35f, 0.35f)
                                        .build(),
                                    lifecycleOwner
                                )
                                try {
                                    bindToLifecycle(listOf(primaryConfig, secondaryConfig))
                                } catch (e: Exception) {
                                    Log.w("VideoRecorder", "Concurrent camera binding failed, fallback to back camera", e)
                                    try {
                                        val fallbackGroup = UseCaseGroup.Builder()
                                            .setViewPort(viewPort)
                                            .addUseCase(videoCapture!!)
                                            .build()
                                        bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, fallbackGroup)
                                    } catch (_: Exception) {
                                        bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, videoCapture!!)
                                    }
                                }
                            }
                        } else {
                            Log.w("VideoRecorder", "Concurrent camera unsupported, fallback to back camera")
                            try {
                                val fallbackGroup = UseCaseGroup.Builder()
                                    .setViewPort(viewPort)
                                    .addUseCase(videoCapture!!)
                                    .build()
                                bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, fallbackGroup)
                            } catch (_: Exception) {
                                bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, videoCapture!!)
                            }
                        }
                    } else {
                        try {
                            val singleGroup = UseCaseGroup.Builder()
                                .setViewPort(viewPort)
                                .addUseCase(videoCapture!!)
                                .build()
                            bindToLifecycle(
                                lifecycleOwner, cameraType.cameraSelector, singleGroup
                            )
                        } catch (e: Exception) {
                            Log.w("VideoRecorder", "Single group binding failed, fallback to direct useCase", e)
                            bindToLifecycle(
                                lifecycleOwner, cameraType.cameraSelector, videoCapture!!
                            )
                        }
                    }
                }
            }

            try {
                val mediaStoreOutput = FileOutputOptions
                    .Builder(outputFile)
                    .build()

                try {
                    recording = videoCapture!!.output
                        .prepareRecording(context, mediaStoreOutput)
                        .withAudioEnabled()
                        .start(
                            Dispatchers.IO.asExecutor(),
                            listener
                        )
                } catch (audioEx: SecurityException) {
                    Log.w("VideoRecorder", "Audio record permission missing, recording video only", audioEx)
                    recording = videoCapture!!.output
                        .prepareRecording(context, mediaStoreOutput)
                        .start(
                            Dispatchers.IO.asExecutor(),
                            listener
                        )
                }

                if (secondaryVideoCapture != null && secondaryOutputFile != null) {
                    val secondaryMediaStoreOutput = FileOutputOptions
                        .Builder(secondaryOutputFile)
                        .build()

                    secondaryRecording = secondaryVideoCapture!!.output
                        .prepareRecording(context, secondaryMediaStoreOutput)
                        .start(
                            Dispatchers.IO.asExecutor()
                        ) { event ->
                            if (event is VideoRecordEvent.Finalize) {
                                if (event.hasError()) {
                                    Log.e("VideoRecorder", "Secondary recording finalized with error: ${event.error}, cause: ${event.cause}")
                                }
                                secondaryFinalizeDeferred?.complete(Unit)
                            }
                        }
                }

                toRecordState()
            } catch (e: Exception) {
                Log.e("VideoRecorder", "Failed to start recording session", e)
                _recordState.update { RecorderState.Idle }
            }
        }
    }

    override fun pauseRecord() {
        scope.launch {
            recording?.run {
                pause()
            }
            secondaryRecording?.run {
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
            secondaryRecording?.run {
                resume()
            }

            toRecordState()
        }
    }

    override suspend fun stopRecordAndDestroyRecorder() {
        val primDeferred = CompletableDeferred<Unit>()
        val secDeferred = if (secondaryRecording != null) CompletableDeferred<Unit>() else null
        primaryFinalizeDeferred = primDeferred
        secondaryFinalizeDeferred = secDeferred

        val hasPrimary = recording != null
        val hasSecondary = secondaryRecording != null

        try {
            recording?.stop()
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Error stopping primary recording", e)
            primDeferred.complete(Unit)
        }

        try {
            secondaryRecording?.stop()
        } catch (e: Exception) {
            Log.e("VideoRecorder", "Error stopping secondary recording", e)
            secDeferred?.complete(Unit)
        }

        try {
            // Wait for CameraX to finalize and write the MP4 headers/moov atom to disk
            if (hasPrimary) {
                withTimeoutOrNull(5000L) {
                    primDeferred.await()
                }
            }
            if (hasSecondary && secDeferred != null) {
                withTimeoutOrNull(5000L) {
                    secDeferred.await()
                }
            }
        } finally {
            try {
                recording?.close()
            } catch (_: Exception) {}
            try {
                secondaryRecording?.close()
            } catch (_: Exception) {}

            cameraProvider?.run {
                withContext(NonCancellable) {
                    withContext(Dispatchers.Main) {
                        try {
                            unbindAll()
                        } catch (_: Exception) {}
                    }
                }
            }

            recording = null
            secondaryRecording = null
            primaryFinalizeDeferred = null
            secondaryFinalizeDeferred = null
            cameraProvider = null
            videoCapture = null
            secondaryVideoCapture = null

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