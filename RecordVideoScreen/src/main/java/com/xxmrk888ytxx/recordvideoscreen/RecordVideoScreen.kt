package com.xxmrk888ytxx.recordvideoscreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.camerapreviewcompose.CameraPreview
import com.xxmrk888ytxx.camerapreviewcompose.models.CameraType
import com.xxmrk888ytxx.corecompose.LocalTheme
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButton
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButtonHolderWidget
import com.xxmrk888ytxx.corecompose.Shared.RecordStateWidget
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestPermissionDialog
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestedPermissionModel
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import com.xxmrk888ytxx.recordvideoscreen.models.ViewType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RecordVideoScreen(
    recordVideoViewModel: RecordVideoViewModel,
) {

    val requestRecordAudioPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = recordVideoViewModel::onRecordAudioPermissionResult
    )

    val requestCameraPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = recordVideoViewModel::onCameraPermissionResult
    )

    val requestPostNotificationPermissionContract = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = recordVideoViewModel::onPostNotificationPermissionResult
        )
    } else {
        null
    }

    val dialogState by recordVideoViewModel.dialogState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit, block = {
        recordVideoViewModel.initActivityResultContracts(
            requestAudioRecordPermissionContract = requestRecordAudioPermissionContract,
            requestCameraPermissionContract = requestCameraPermissionContract,
            requestPostNotificationPermissionContract = requestPostNotificationPermissionContract
        )
    })

    val recordState by recordVideoViewModel.currentRecordState.collectAsStateWithLifecycle()

    val currentWidgetGradientColor by recordVideoViewModel.currentWidgetColor.collectAsStateWithLifecycle()

    val themeValues = LocalTheme.current.values

    val viewType by recordVideoViewModel.viewType.collectAsStateWithLifecycle()

    val animatedWidgetGradientColor by animateColorAsState(
        targetValue = currentWidgetGradientColor.color,
        animationSpec = tween(
            durationMillis = themeValues.animationDuration
        )
    )

    var alpha by rememberSaveable {
        mutableStateOf(1f)
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = alpha,
        animationSpec = tween(themeValues.animationDuration)
    )

    LaunchedEffect(key1 = recordState::class, block = {
        launch {
            if (recordState is RecordState.Idle) {
                alpha = 1f
            }
            while (recordState is RecordState.Recording) {
                recordVideoViewModel.regenerateButtonGradientColor()
                alpha = if (alpha == 1f) 0f else 1f
                delay((themeValues.animationDuration + themeValues.additionalAnimationDuration).toLong())
            }
        }
    })

    Scaffold(
        Modifier
            .fillMaxSize()
        ,
        backgroundColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding()
                )
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                AnimatedContent(
                    targetState = viewType
                ) { state ->
                    when(state) {
                        is ViewType.RecordWidget -> {
                            RecordStateWidget(
                                isRecordEnabled = recordState !is RecordState.Idle,
                                recordTime = recordState.currentDuration,
                                modifier = Modifier
                                    .fillMaxWidth()
                                ,
                                borderWhenRecordEnabled = animatedWidgetGradientColor,
                                icon = {
                                    StyleIcon(
                                        painter = painterResource(
                                            id = R.drawable.videocam
                                        ),
                                        tint = themeColors.iconsColor.copy(alpha = animatedAlpha),
                                        size = themeDimensions.iconSize
                                    )
                                }
                            )
                        }

                        is ViewType.CameraPreview -> {
                            CameraPreview(
                                cameraType = CameraType.Back,
                                modifier = Modifier.fillMaxWidth()
                                    .fillMaxHeight(0.5f)
                            )
                        }
                    }
                }
            }

            item {
                ControlRecordButtonHolderWidget(
                    modifier = Modifier.padding(
                        top = if(viewType is ViewType.RecordWidget)
                            themeDimensions.controlRecordButtonHolderWidgetPadding
                        else themeDimensions.controlRecordButtonHolderCameraPreviewPadding
                    )
                ) {

                    ControlRecordButton(
                        painter = painterResource(id = R.drawable.baseline_visibility_24),
                        background = themeColors.supportControlRecordButtonColor
                    ) {
                        if(viewType is ViewType.RecordWidget) {
                            recordVideoViewModel.toCameraPreviewType()
                        } else {
                            recordVideoViewModel.toRecordWidgetViewType()
                        }
                    }

                    AnimatedVisibility(visible = recordState !is RecordState.Idle) {
                        ControlRecordButton(
                            painter = if(recordState is RecordState.Recording)
                                painterResource(id = R.drawable.pause)
                            else
                                painterResource(id = R.drawable.play),
                            background = themeColors.supportControlRecordButtonColor
                        ) {
                            if(recordState is RecordState.Recording) {
                                recordVideoViewModel.pauseRecord()
                            } else {
                                recordVideoViewModel.resumeRecord()
                            }
                        }
                    }

                    ControlRecordButton(
                        painter = painterResource(
                            id = if (recordState !is RecordState.Recording) R.drawable.videocam
                            else R.drawable.baseline_stop_24
                        ),
                        background = themeColors.recordButtonColor
                    ) {
                        if (recordState is RecordState.Recording) {
                            recordVideoViewModel.stopRecord()
                        } else {
                            recordVideoViewModel.startRecord()
                        }
                    }

                    ControlRecordButton(
                        painter = painterResource(id = R.drawable.rotation),
                        background = themeColors.supportControlRecordButtonColor
                    ) {

                    }
                }
            }


        }
    }

    if(dialogState.isPermissionStateVisible) {
        RequestPermissionDialog(
            permissions = requestedPermission(recordVideoViewModel),
            onDismissRequest = recordVideoViewModel::hidePermissionDialog
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun requestedPermission(recordVideoViewModel:RecordVideoViewModel) : List<RequestedPermissionModel> {
    val audioPermission by recordVideoViewModel.recordAudioPermissionState.collectAsStateWithLifecycle()

    val cameraPermission by recordVideoViewModel.cameraPermissionState.collectAsStateWithLifecycle()

    val postNotificationPermission by recordVideoViewModel
        .postNotificationPermissionState.collectAsStateWithLifecycle()

    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        listOf(audioPermission,cameraPermission,postNotificationPermission)
    else listOf(audioPermission,cameraPermission)
}