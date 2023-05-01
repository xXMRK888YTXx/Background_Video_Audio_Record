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

/**
 * [Ru]
 * Виджет превью с камеры
 *
 * @param cameraType - камера с которой будет выводится видео
 *
 * @param isRecord - значение, которое отвечает ведётся ли запись,
 * из другого места(например, съемка с видео)
 *
 * @param contentWhenRecordEnabled - содержимое которое будет показано, пока идет запись
 * из другого места
 *
 *
 * Данный виджет следит за жизненным циклом, и при выходе из приложения или
 * когда виджет покинет композицию, он отвяжется от камеры.
 *
 * Так же очень важно, передать isRecord = true, когда вы начинаете запись из другого
 * места, так как, из за работы api при подключении к камеры с разными [LifecycleOwner]
 * текущая запись будет прервана. После передачи isRecord = true, виджет сам отключится от
 * камеры, и когда isRecord = false, так же переподключится к камере
 */

/**
 * [En]
 * Camera preview widget
 *
 * @param cameraType - camera from which video will be displayed
 *
 * @param isRecord - the value which answers whether the video is being recorded,
 * from another location (e.g. video capture)
 *
 * @param contentWhenRecordEnabled - the content which will be shown while recording
 * from another location
 *
 *
 * This widget keeps track of the lifecycle, and when you exit the application or
 * when the widget leaves the composition, it will detach from the camera.
 *
 * It is also very important to pass isRecord = true when you start recording from a different
 * place, because, because of the way the api works, when you connect to a camera with different [LifecycleOwner]
 * the current recording will be interrupted. After passing isRecord = true, the widget itself will disconnect from the
 * camera, and when isRecord = false, will also reconnect to the camera
 */
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