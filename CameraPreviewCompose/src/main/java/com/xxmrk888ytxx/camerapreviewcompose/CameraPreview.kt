package com.xxmrk888ytxx.camerapreviewcompose

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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.xxmrk888ytxx.camerapreviewcompose.models.CameraType
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition

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

    LaunchedEffect(cameraType, isRecord, isActive, mainCamera, cameraProvider, localLifecycleOwner, primaryPreviewView, secondaryPreviewView) {
        val provider = cameraProvider ?: return@LaunchedEffect
        try {
            provider.unbindAll()
        } catch (_: Exception) {}

        if (!isRecord && isActive) {
            try {
                primaryPreview.setSurfaceProvider(primaryPreviewView.surfaceProvider)
                if (isDual) {
                    secondaryPreview.setSurfaceProvider(secondaryPreviewView.surfaceProvider)
                }
                bindPreview(provider, localLifecycleOwner, cameraType, primaryPreview, secondaryPreview, mainCamera)
            } catch (_: Exception) {}
        }
    }

    DisposableEffect(cameraProvider) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
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
                        factory = { primaryPreviewView },
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
                                factory = { secondaryPreviewView },
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
                                    text = if (mainCamera == DualCameraPrimary.BACK) "🤳 Селфи" else "📷 Задняя",
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
        cameraProvider.unbindAll()
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
    } catch (_: Exception) {
        try {
            cameraProvider.unbindAll()
            val fallbackSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
            cameraProvider.bindToLifecycle(lifecycleOwner, fallbackSelector, primaryPreview)
        } catch (_: Exception) {}
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
            } catch (_: Exception) {
                value = null
            }
        }, ContextCompat.getMainExecutor(context))
    }.value
}