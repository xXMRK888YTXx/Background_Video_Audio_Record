package com.xxmrk888ytxx.recordaudioscreen

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
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
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.LocalTheme
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButton
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButtonHolderWidget
import com.xxmrk888ytxx.corecompose.Shared.RecordStateWidget
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestPermissionDialog
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestedPermissionModel
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.StyleIconButton
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordAudioScreen(
    recordAudioViewModel: RecordAudioViewModel,
) {

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = scaffoldState, block = {
        recordAudioViewModel.initSnackBarHostState(scaffoldState.snackbarHostState)
    })

    val requestAudioRecordPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = recordAudioViewModel::onRequestAudioRecordPermissionResult
    )

    val requestPostNotificationPermissionContract =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = recordAudioViewModel::onRequestPostNotificationPermissionResult
            )
        } else {
            null
        }

    LaunchedEffect(
        key1 = requestAudioRecordPermissionContract,
        key2 = requestPostNotificationPermissionContract
    ) {
        recordAudioViewModel.initRequestPermissionsContracts(
            requestAudioRecordPermissionContract,
            requestPostNotificationPermissionContract
        )
    }

    val recordState by recordAudioViewModel.recordState.collectAsStateWithLifecycle(
        RecordState.Idle
    )

    val dialogState by recordAudioViewModel.dialogState.collectAsStateWithLifecycle()

    val currentWidgetGradientColor by recordAudioViewModel.currentWidgetColor.collectAsStateWithLifecycle()

    val themeValues = LocalTheme.current.values

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
                recordAudioViewModel.regenerateButtonGradientColor()
                alpha = if (alpha == 1f) 0f else 1f
                delay((themeValues.animationDuration + themeValues.additionalAnimationDuration).toLong())
            }
        }
    })

    Scaffold(
        Modifier
            .fillMaxSize(),
        backgroundColor = Color.Transparent,
        scaffoldState = scaffoldState
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding()
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                RecordStateWidget(
                    recordTime = recordState.recordDuration,
                    isRecordEnabled = recordState !is RecordState.Idle,
                    modifier = Modifier
                        .fillMaxWidth(),
                    borderWhenRecordEnabled = animatedWidgetGradientColor,
                    icon = {
                        StyleIcon(
                            painter = painterResource(
                                id = R.drawable.baseline_mic_24
                            ),
                            tint = themeColors.iconsColor.copy(alpha = animatedAlpha),
                            size = themeDimensions.iconSize
                        )
                    }
                )
            }

            item {
                ControlRecordButtonHolderWidget(
                    modifier = Modifier.padding(
                        top = themeDimensions.controlRecordButtonHolderWidgetPadding
                    )
                ) {
                    AnimatedVisibility(visible = recordState !is RecordState.Idle) {
                        ControlRecordButton(
                            painter = if(recordState is RecordState.Recording)
                                painterResource(id = R.drawable.pause)
                            else
                                painterResource(id = R.drawable.play),
                            background = themeColors.supportControlRecordButtonColor
                        ) {
                            if(recordState is RecordState.Recording) {
                                recordAudioViewModel.pauseRecord()
                            } else {
                                recordAudioViewModel.resumeRecord()
                            }
                        }
                    }

                    ControlRecordButton(
                        painter = painterResource(
                            id = if (recordState is RecordState.Idle) R.drawable.baseline_mic_24
                            else R.drawable.baseline_stop_24
                        ),
                        background = themeColors.recordButtonColor
                    ) {
                        if (recordState !is RecordState.Idle) {
                            recordAudioViewModel.stopRecord()
                        } else {
                            recordAudioViewModel.startRecord()
                        }
                    }
                }
            }
        }
    }

    if (dialogState.isPermissionDialogVisible) {
        RequestPermissionDialog(
            permissions = requestedPermissions(recordAudioViewModel),
            onDismissRequest = recordAudioViewModel::hideRequestPermissionDialog
        )
    }

}

@SuppressLint("NewApi")
@Composable
internal fun requestedPermissions(
    recordAudioViewModel: RecordAudioViewModel,
): List<RequestedPermissionModel> {
    val recordAudio by recordAudioViewModel.recordAudioPermissionState.collectAsStateWithLifecycle()

    val postNotification by recordAudioViewModel.postNotificationPermissionState.collectAsStateWithLifecycle()

    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) listOf(recordAudio,postNotification)
        else listOf(recordAudio)
}