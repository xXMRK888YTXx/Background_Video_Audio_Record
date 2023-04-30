package com.xxmrk888ytxx.camerapreviewcompose

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.xxmrk888ytxx.camerapreviewcompose.models.CameraType

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraType: CameraType
) {
    val localLifecycleOwner = LocalLifecycleOwner.current

    val cameraProvider = rememberCameraProvider()
    val preview = rememberPreview()

    AndroidView(
        factory = {
            val view = PreviewView(it)

            cameraProvider.bindToLifecycle(
                localLifecycleOwner, cameraType.cameraSelector, preview
            )

            preview.setSurfaceProvider(view.surfaceProvider)

            view
        },
        modifier = modifier

    )

    DisposableEffect(key1 = Unit, effect = {
        onDispose { cameraProvider.unbind(preview) }
    })
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