package com.xxmrk888ytxx.camerapreviewcompose

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.xxmrk888ytxx.camerapreviewcompose.models.CameraType

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraType: CameraType,
    isRecord:Boolean,
    contentWhenRecordEnabled:@Composable () -> Unit
) {
    val localLifecycleOwner = LocalLifecycleOwner.current

    val cameraProvider = rememberCameraProvider()
    val preview = rememberPreview()

    LaunchedEffect(key1 = cameraType, block = {

        if(cameraProvider.isBound(preview) && !isRecord) {
            cameraProvider.unbind(preview)
        }

        cameraProvider.bindToLifecycle(
            localLifecycleOwner, cameraType.cameraSelector, preview
        )
    })

    AnimatedContent(targetState = isRecord) { isRecording ->
        if(!isRecording) {
            AndroidView(
                factory = {
                    val view = PreviewView(it)

                    if(!cameraProvider.isBound(preview) && !isRecord) {
                        cameraProvider.bindToLifecycle(
                            localLifecycleOwner, cameraType.cameraSelector, preview
                        )
                    }

                    preview.setSurfaceProvider(view.surfaceProvider)

                    view
                },
                modifier = modifier

            )

            DisposableEffect(key1 = Unit, effect = {
                onDispose { cameraProvider.unbind(preview) }
            })
        } else {
            contentWhenRecordEnabled()
        }
    }
}

@Composable
internal fun rememberCameraProvider() :  ProcessCameraProvider {
    val context = LocalContext.current

    val cameraProvider = remember {
        ProcessCameraProvider.getInstance(context.applicationContext).get()
    }

    return cameraProvider
}

@Composable
internal fun rememberPreview() : Preview {
    return remember {
        Preview.Builder().build()
    }
}