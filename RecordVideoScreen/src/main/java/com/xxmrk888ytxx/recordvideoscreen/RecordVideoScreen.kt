package com.xxmrk888ytxx.recordvideoscreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.camerapreviewcompose.CameraPreview
import com.xxmrk888ytxx.camerapreviewcompose.models.CameraType
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.corecompose.Shared.BatteryIgnoreOptimizationDialog
import com.xxmrk888ytxx.corecompose.Shared.RecordStateWidget
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestPermissionDialog
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.models.RequestedPermissionModel
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.LocalCurrentBottomBarPage
import com.xxmrk888ytxx.corecompose.LocalIsPageActive
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.recordvideoscreen.models.CurrentSelectedCameraModel
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import com.xxmrk888ytxx.recordvideoscreen.models.ViewType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun RecordVideoScreen(
    recordVideoViewModel: RecordVideoViewModel,
) {
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit) {
        recordVideoViewModel.initSnackBarHostState(scaffoldState.snackbarHostState)
    }

    val requestRecordAudioPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = recordVideoViewModel::onRecordAudioPermissionResult
    )

    val requestCameraPermissionContract = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = recordVideoViewModel::onCameraPermissionResult
    )

    val requestPostNotificationPermissionContract =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = recordVideoViewModel::onPostNotificationPermissionResult
            )
        } else null

    val dialogState by recordVideoViewModel.dialogState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        recordVideoViewModel.initActivityResultContracts(
            requestAudioRecordPermissionContract = requestRecordAudioPermissionContract,
            requestCameraPermissionContract = requestCameraPermissionContract,
            requestPostNotificationPermissionContract = requestPostNotificationPermissionContract
        )
    }

    val recordState by recordVideoViewModel.currentRecordState.collectAsStateWithLifecycle()
    val isRecordStarting by recordVideoViewModel.isRecordStarting.collectAsStateWithLifecycle()
    val isRecordingActive = (recordState !is RecordState.Idle) || isRecordStarting
    val viewType by recordVideoViewModel.viewType.collectAsStateWithLifecycle()
    val currentSelectedCamera by recordVideoViewModel.currentCamera.collectAsStateWithLifecycle()
    val dualCameraPrimary by recordVideoViewModel.dualCameraPrimary.collectAsStateWithLifecycle()
    val pipPosition by recordVideoViewModel.pipPosition.collectAsStateWithLifecycle()

    val isPageVisible = LocalIsPageActive.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent,
        scaffoldState = scaffoldState
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding()
                )
        ) {
            // Modern Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.video_recording_header),
                        color = themeColors.primaryFontColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        val isRecording = recordState is RecordState.Recording
                        val isPaused = recordState is RecordState.Pause
                        val dotColor = when {
                            isRecording -> Color(0xFFEF4444)
                            isPaused -> Color(0xFFF59E0B)
                            else -> Color(0xFF10B981)
                        }
                        PulsingStatusDot(
                            isRecording = isRecording,
                            dotColor = dotColor,
                            modifier = Modifier.size(8.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (recordState) {
                                is RecordState.Recording -> "${stringResource(R.string.video_recording_prefix)} (${recordState.currentDuration.milliSecondToString()})"
                                is RecordState.Pause -> stringResource(R.string.video_on_pause)
                                else -> stringResource(R.string.video_ready_for_background)
                            },
                            color = when (recordState) {
                                is RecordState.Recording -> Color(0xFFEF4444)
                                is RecordState.Pause -> Color(0xFFF59E0B)
                                else -> themeColors.secondFontColor
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // View switcher pill button (Превью / Круг)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(themeColors.cardColor)
                        .border(1.dp, Color(0xFF263346), RoundedCornerShape(20.dp))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isPreviewSelected = viewType is ViewType.CameraPreview
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isPreviewSelected) themeColors.primaryColor else Color.Transparent)
                            .clickable { recordVideoViewModel.toCameraPreviewType() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.preview_button),
                            color = if (isPreviewSelected) Color.White else themeColors.secondFontColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (!isPreviewSelected) themeColors.primaryColor else Color.Transparent)
                            .clickable { recordVideoViewModel.toRecordWidgetViewType() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.screen_button),
                            color = if (!isPreviewSelected) Color.White else themeColors.secondFontColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Center Viewport: fills all available space cleanly without scrolling
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = viewType,
                    animationSpec = tween(180)
                ) { state ->
                    when (state) {
                        is ViewType.RecordWidget -> {
                            RecordingHudCard(
                                recordState = recordState,
                                currentSelectedCamera = currentSelectedCamera,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        is ViewType.CameraPreview -> {
                            CameraPreview(
                                cameraType = when (currentSelectedCamera) {
                                    is CurrentSelectedCameraModel.Back -> CameraType.Back
                                    is CurrentSelectedCameraModel.Front -> CameraType.Front
                                    is CurrentSelectedCameraModel.Both -> CameraType.Both
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(24.dp))
                                    .border(
                                        1.dp,
                                        if (isRecordingActive) Color(0xFFEF4444) else Color(0xFF263346),
                                        RoundedCornerShape(24.dp)
                                    ),
                                isRecord = isRecordingActive,
                                isActive = isPageVisible,
                                mainCamera = dualCameraPrimary,
                                pipPosition = pipPosition
                            ) {
                                RecordingHudCard(
                                    recordState = recordState,
                                    currentSelectedCamera = currentSelectedCamera,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Bottom Controls Section (Ergonomic Thumb Zone)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Camera Selector: placed comfortably right above the record button
                CameraModeSelector(
                    selectedCamera = currentSelectedCamera,
                    isRecording = recordState !is RecordState.Idle,
                    isConcurrentSupported = recordVideoViewModel.isConcurrentCameraSupported,
                    onSelectCamera = { recordVideoViewModel.selectCamera(it) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Ergonomic Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Secondary action left: View toggle button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .clickable {
                                if (viewType is ViewType.RecordWidget) {
                                    recordVideoViewModel.toCameraPreviewType()
                                } else {
                                    recordVideoViewModel.toRecordWidgetViewType()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = if (viewType is ViewType.RecordWidget)
                                painterResource(R.drawable.baseline_visibility_24)
                            else painterResource(R.drawable.baseline_visibility_off_24),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Main Ergonomic Shutter Button (Large 76dp)
                    val isRecording = recordState !is RecordState.Idle
                    val shutterColor = if (isRecording) Color(0xFFEF4444) else Color(0xFFE11D48)

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .border(3.dp, shutterColor.copy(alpha = 0.6f), CircleShape)
                            .padding(5.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(shutterColor, shutterColor.copy(alpha = 0.85f))
                                )
                            )
                            .clickable {
                                if (isRecording) {
                                    recordVideoViewModel.stopRecord()
                                } else {
                                    recordVideoViewModel.startRecord()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRecording) {
                            // Stop square
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                            )
                        } else {
                            // Record camera icon
                            Icon(
                                painter = painterResource(id = R.drawable.videocam),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Secondary action right: Pause/Resume (when recording) or Flip camera (when idle)
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), CircleShape)
                            .clickable {
                                if (isRecording) {
                                    if (recordState is RecordState.Recording) {
                                        recordVideoViewModel.pauseRecord()
                                    } else {
                                        recordVideoViewModel.resumeRecord()
                                    }
                                } else {
                                    recordVideoViewModel.changeCurrentSelectedCamera(currentSelectedCamera)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRecording) {
                            Icon(
                                painter = if (recordState is RecordState.Recording)
                                    painterResource(id = R.drawable.pause)
                                else
                                    painterResource(id = R.drawable.play),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.rotation),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (dialogState.isPermissionStateVisible) {
        RequestPermissionDialog(
            permissions = requestedPermission(recordVideoViewModel),
            onDismissRequest = recordVideoViewModel::hidePermissionDialog
        )
    }

    if (dialogState.isIgnoreBatteryOptimizationDialogVisible) {
        BatteryIgnoreOptimizationDialog(
            onHide = {
                recordVideoViewModel.hideIgnoreBatteryOptimizationDialog(it)
            },
            onConfirm = {
                recordVideoViewModel.openBatterySettings()
                recordVideoViewModel.hideIgnoreBatteryOptimizationDialog(it)
            }
        )
    }
}

@SuppressLint("NewApi")
@Composable
fun requestedPermission(recordVideoViewModel: RecordVideoViewModel): ImmutableList<RequestedPermissionModel> {
    val audioPermission by recordVideoViewModel.recordAudioPermissionState.collectAsStateWithLifecycle()
    val cameraPermission by recordVideoViewModel.cameraPermissionState.collectAsStateWithLifecycle()
    val postNotificationPermission by recordVideoViewModel
        .postNotificationPermissionState.collectAsStateWithLifecycle()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        persistentListOf(audioPermission, cameraPermission, postNotificationPermission)
    else persistentListOf(audioPermission, cameraPermission)
}

@Composable
private fun CameraModeSelector(
    selectedCamera: CurrentSelectedCameraModel,
    isRecording: Boolean,
    isConcurrentSupported: Boolean,
    onSelectCamera: (CurrentSelectedCameraModel) -> Unit
) {
    val modes = listOf(
        CurrentSelectedCameraModel.Back to stringResource(R.string.back),
        CurrentSelectedCameraModel.Front to stringResource(R.string.front),
        CurrentSelectedCameraModel.Both to if (isConcurrentSupported) "✨ " + stringResource(R.string.both_cameras) else stringResource(R.string.both_cameras)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(themeColors.cardColor)
                .border(1.dp, Color(0xFF263346), RoundedCornerShape(24.dp))
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            modes.forEach { (mode, label) ->
                val isSelected = selectedCamera == mode
                val animatedBg by animateColorAsState(
                    targetValue = if (isSelected) themeColors.primaryColor else Color.Transparent,
                    animationSpec = tween(200),
                    label = "mode_bg"
                )
                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else themeColors.secondFontColor,
                    animationSpec = tween(200),
                    label = "mode_text"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(animatedBg)
                        .clickable(enabled = !isRecording) { onSelectCamera(mode) }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = animatedTextColor,
                        style = themeTypography.body.copy(
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }

        if (selectedCamera is CurrentSelectedCameraModel.Both && !isConcurrentSupported) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x26F59E0B))
                    .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚠️ " + stringResource(R.string.concurrent_camera_not_supported),
                    color = Color(0xFFFBBF24),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PulsingStatusDot(
    isRecording: Boolean,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    if (isRecording) {
        val infiniteTransition = rememberInfiniteTransition(label = "rec_pulse")
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
        Box(
            modifier = modifier
                .graphicsLayer { alpha = pulseAlpha }
                .clip(CircleShape)
                .background(dotColor)
        )
    } else {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}

@Composable
private fun RecordingHudCard(
    recordState: RecordState,
    currentSelectedCamera: CurrentSelectedCameraModel,
    modifier: Modifier = Modifier
) {
    val isRecording = recordState is RecordState.Recording
    val isPaused = recordState is RecordState.Pause
    val isIdle = recordState is RecordState.Idle

    val borderColor = when {
        isRecording -> Color(0xFFEF4444)
        isPaused -> Color(0xFFF59E0B)
        else -> Color(0xFF263346)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F172A))
            .border(1.5.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Status pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        when {
                            isRecording -> Color(0x33EF4444)
                            isPaused -> Color(0x33F59E0B)
                            else -> Color(0x2210B981)
                        }
                    )
                    .border(
                        1.dp,
                        when {
                            isRecording -> Color(0x66EF4444)
                            isPaused -> Color(0x66F59E0B)
                            else -> Color(0x4410B981)
                        },
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                PulsingStatusDot(
                    isRecording = isRecording,
                    dotColor = when {
                        isRecording -> Color(0xFFEF4444)
                        isPaused -> Color(0xFFF59E0B)
                        else -> Color(0xFF10B981)
                    },
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        isRecording -> stringResource(R.string.status_pill_recording)
                        isPaused -> stringResource(R.string.status_pill_paused)
                        else -> stringResource(R.string.status_pill_standby)
                    },
                    color = when {
                        isRecording -> Color(0xFFEF4444)
                        isPaused -> Color(0xFFF59E0B)
                        else -> Color(0xFF10B981)
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Time counter
            Text(
                text = if (isIdle) "00:00:00" else recordState.currentDuration.milliSecondToString(),
                color = if (isIdle) Color(0xFF94A3B8) else Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            val cameraLabel = when (currentSelectedCamera) {
                is CurrentSelectedCameraModel.Both -> stringResource(R.string.camera_dual_label)
                is CurrentSelectedCameraModel.Back -> stringResource(R.string.camera_main_label)
                is CurrentSelectedCameraModel.Front -> stringResource(R.string.camera_front_label)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = cameraLabel,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isIdle) {
                    stringResource(R.string.camera_ready_hint)
                } else {
                    stringResource(R.string.camera_recording_hint)
                },
                color = themeColors.secondFontColor,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}