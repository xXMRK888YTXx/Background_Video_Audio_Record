package com.xxmrk888ytxx.camerapreviewcompose

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ConcurrentCamera
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.xxmrk888ytxx.camerapreviewcompose.models.CameraType
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import kotlinx.coroutines.delay

private const val TAG = "CameraPreview"

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraType: CameraType,
    isRecord: Boolean,
    isActive: Boolean = true,
    mainCamera: DualCameraPrimary = DualCameraPrimary.BACK,
    pipPosition: PipPosition = PipPosition.TOP_RIGHT,
    contentWhenRecordEnabled: @Composable () -> Unit
) {
    val localLifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProvider = produceCameraProvider()

    // Persistent PreviewViews with TextureView (COMPATIBLE) mode to eliminate SurfaceView punch-through black holes
    val primaryPreviewView = remember(context) {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val secondaryPreviewView = remember(context) {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    val primaryPreview = remember(cameraType, mainCamera) { Preview.Builder().build() }
    val secondaryPreview = remember(cameraType, mainCamera) { Preview.Builder().build() }

    val isDual = cameraType is CameraType.Both

    var resumeTrigger by remember { mutableStateOf(0) }

    DisposableEffect(localLifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resumeTrigger++
            }
        }
        localLifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            localLifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(
        cameraType,
        isRecord,
        isActive,
        mainCamera,
        cameraProvider,
        localLifecycleOwner,
        primaryPreviewView,
        secondaryPreviewView,
        resumeTrigger
    ) {
        val provider = cameraProvider ?: return@LaunchedEffect

        // Always detach surface providers first to signal CameraX that previous surfaces are reset
        try {
            primaryPreview.setSurfaceProvider(null)
            secondaryPreview.setSurfaceProvider(null)
            provider.unbind(primaryPreview, secondaryPreview)
        } catch (_: Exception) {}

        if (!isRecord && isActive) {
            // Robust retry loop with backoff to handle camera hardware release latency
            // (e.g. background service finalizing MP4 or camera HAL session transition)
            var bound = false
            var attempt = 0
            val maxAttempts = 5

            while (!bound && attempt < maxAttempts && !isRecord && isActive) {
                attempt++
                try {
                    // Reset surface providers to force a fresh SurfaceRequest
                    primaryPreview.setSurfaceProvider(null)
                    if (isDual) {
                        secondaryPreview.setSurfaceProvider(null)
                    }
                    provider.unbind(primaryPreview, secondaryPreview)

                    primaryPreview.setSurfaceProvider(primaryPreviewView.surfaceProvider)
                    if (isDual) {
                        secondaryPreview.setSurfaceProvider(secondaryPreviewView.surfaceProvider)
                    }

                    bindPreview(
                        cameraProvider = provider,
                        lifecycleOwner = localLifecycleOwner,
                        cameraType = cameraType,
                        primaryPreview = primaryPreview,
                        secondaryPreview = secondaryPreview,
                        mainCamera = mainCamera
                    )
                    bound = true
                    Log.d(TAG, "Camera preview bound successfully on attempt $attempt")
                } catch (e: Exception) {
                    Log.w(TAG, "Camera preview bind attempt $attempt failed: ${e.message}")
                    if (attempt < maxAttempts) {
                        delay(attempt * 150L)
                    }
                }
            }
        }
    }

    DisposableEffect(cameraProvider, primaryPreview, secondaryPreview) {
        onDispose {
            try {
                primaryPreview.setSurfaceProvider(null)
                secondaryPreview.setSurfaceProvider(null)
                cameraProvider?.unbind(primaryPreview, secondaryPreview)
            } catch (_: Exception) {}
        }
    }

    Crossfade(
        targetState = isRecord,
        animationSpec = tween(150),
        modifier = modifier
    ) { isRecording ->
        if (!isRecording) {
            if (cameraProvider != null && isActive) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Primary camera view (Fullscreen)
                    AndroidView(
                        factory = {
                            (primaryPreviewView.parent as? ViewGroup)?.removeView(primaryPreviewView)
                            primaryPreviewView
                        },
                        update = { view ->
                            try {
                                primaryPreview.setSurfaceProvider(view.surfaceProvider)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Secondary camera view: live PiP overlay when in Both mode
                    if (isDual) {
                        val pipAlignment = when (pipPosition) {
                            PipPosition.TOP_RIGHT -> Alignment.TopEnd
                            PipPosition.TOP_LEFT -> Alignment.TopStart
                            PipPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
                            PipPosition.BOTTOM_LEFT -> Alignment.BottomStart
                        }
                        Box(
                            modifier = Modifier
                                .align(pipAlignment)
                                .padding(14.dp)
                                .size(110.dp, 160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF0F172A))
                                .border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
                        ) {
                            AndroidView(
                                factory = {
                                    (secondaryPreviewView.parent as? ViewGroup)?.removeView(secondaryPreviewView)
                                    secondaryPreviewView
                                },
                                update = { view ->
                                    try {
                                        secondaryPreview.setSurfaceProvider(view.surfaceProvider)
                                    } catch (_: Exception) {}
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Chic badge on PiP window
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xCC0F172A))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (mainCamera == DualCameraPrimary.BACK) {
                                        stringResource(com.xxmrk888ytxx.coreandroid.R.string.camera_selfie_chip)
                                    } else {
                                        stringResource(com.xxmrk888ytxx.coreandroid.R.string.camera_back_chip)
                                    },
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A)))
            }
        } else {
            contentWhenRecordEnabled()
        }
    }
}

private fun bindPreview(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    cameraType: CameraType,
    primaryPreview: Preview,
    secondaryPreview: Preview,
    mainCamera: DualCameraPrimary = DualCameraPrimary.BACK
) {
    try {
        cameraProvider.unbind(primaryPreview, secondaryPreview)
        if (cameraType is CameraType.Both) {
            val concurrentCameras = cameraProvider.availableConcurrentCameraInfos
            if (concurrentCameras.isNotEmpty()) {
                var backSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                var frontSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                for (cameraInfoList in concurrentCameras) {
                    for (cameraInfo in cameraInfoList) {
                        if (cameraInfo.lensFacing == CameraSelector.LENS_FACING_BACK) {
                            backSelector = cameraInfo.cameraSelector
                        } else if (cameraInfo.lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            frontSelector = cameraInfo.cameraSelector
                        }
                    }
                }

                val primarySelector = if (mainCamera == DualCameraPrimary.BACK) backSelector else frontSelector
                val secondarySelector = if (mainCamera == DualCameraPrimary.BACK) frontSelector else backSelector

                val primaryGroup = UseCaseGroup.Builder().addUseCase(primaryPreview).build()
                val secondaryGroup = UseCaseGroup.Builder().addUseCase(secondaryPreview).build()

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
                cameraProvider.bindToLifecycle(listOf(primaryConfig, secondaryConfig))
            } else {
                val fallbackSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                    CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
                cameraProvider.bindToLifecycle(lifecycleOwner, fallbackSelector, primaryPreview)
            }
        } else {
            val targetSelector = cameraType.cameraSelector
            val safeSelector = if (cameraProvider.hasCamera(targetSelector)) {
                targetSelector
            } else if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
            cameraProvider.bindToLifecycle(lifecycleOwner, safeSelector, primaryPreview)
        }
    } catch (e: Exception) {
        Log.w(TAG, "Primary or concurrent binding failed, attempting fallback: ${e.message}", e)
        try {
            cameraProvider.unbind(primaryPreview, secondaryPreview)
            val fallbackSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
            cameraProvider.bindToLifecycle(lifecycleOwner, fallbackSelector, primaryPreview)
        } catch (fallbackEx: Exception) {
            Log.e(TAG, "Fallback binding also failed", fallbackEx)
            throw fallbackEx
        }
    }
}

@Composable
internal fun produceCameraProvider(): ProcessCameraProvider? {
    val context = LocalContext.current
    return produceState<ProcessCameraProvider?>(initialValue = null, key1 = context) {
        val future = ProcessCameraProvider.getInstance(context.applicationContext)
        future.addListener({
            try {
                value = future.get()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get ProcessCameraProvider", e)
                value = null
            }
        }, ContextCompat.getMainExecutor(context))
    }.value
}