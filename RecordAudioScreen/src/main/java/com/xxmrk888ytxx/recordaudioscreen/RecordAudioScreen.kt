package com.xxmrk888ytxx.recordaudioscreen

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.corecompose.LocalTheme
import com.xxmrk888ytxx.corecompose.Shared.BatteryIgnoreOptimizationDialog
import com.xxmrk888ytxx.corecompose.Shared.RequestPermissionDialog.RequestPermissionDialog
import com.xxmrk888ytxx.corecompose.Shared.models.RequestedPermissionModel
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.recordaudioscreen.models.RecordState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
        Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent,
        scaffoldState = scaffoldState
    ) { padding ->
        Column(
            Modifier
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.audio_recording_header),
                        color = themeColors.primaryFontColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when (recordState) {
                            is RecordState.Recording -> stringResource(R.string.background_audio_recording)
                            is RecordState.Paused -> stringResource(R.string.audio_recording_paused)
                            else -> stringResource(R.string.audio_specs_header)
                        },
                        color = when (recordState) {
                            is RecordState.Recording -> Color(0xFFEF4444)
                            is RecordState.Paused -> Color(0xFFF59E0B)
                            else -> themeColors.secondFontColor
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Status Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (recordState) {
                                is RecordState.Recording -> Color(0x33EF4444)
                                is RecordState.Paused -> Color(0x33F59E0B)
                                else -> themeColors.cardColor
                            }
                        )
                        .border(
                            1.dp,
                            when (recordState) {
                                is RecordState.Recording -> Color(0x66EF4444)
                                is RecordState.Paused -> Color(0x66F59E0B)
                                else -> Color(0xFF263346)
                            },
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (recordState) {
                                    is RecordState.Recording -> Color(0xFFEF4444).copy(alpha = animatedAlpha)
                                    is RecordState.Paused -> Color(0xFFF59E0B)
                                    else -> Color(0xFF10B981)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (recordState) {
                            is RecordState.Recording -> "REC"
                            is RecordState.Paused -> stringResource(R.string.status_paused)
                            else -> stringResource(R.string.status_ready)
                        },
                        color = when (recordState) {
                            is RecordState.Recording -> Color(0xFFEF4444)
                            is RecordState.Paused -> Color(0xFFF59E0B)
                            else -> Color(0xFF10B981)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Main Central View Area (Modern Audio HUD Card matching Video Screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                AudioRecordingHudCard(
                    recordState = recordState,
                    activeColor = animatedWidgetGradientColor,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Bottom Action Area (Ergonomic Shutter & Controls matching Video Screen)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp, top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left spacer for symmetric visual balance
                    Spacer(modifier = Modifier.size(50.dp))

                    // Main Ergonomic Shutter Button (76dp)
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
                                    recordAudioViewModel.stopRecord()
                                } else {
                                    recordAudioViewModel.startRecord()
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
                            // Record mic icon
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_mic_24),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // Secondary action right: Pause/Resume (when recording) or balanced spacer
                    if (recordState !is RecordState.Idle) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), CircleShape)
                                .clickable {
                                    if (recordState is RecordState.Recording) {
                                        recordAudioViewModel.pauseRecord()
                                    } else {
                                        recordAudioViewModel.resumeRecord()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = if (recordState is RecordState.Recording)
                                    painterResource(id = R.drawable.pause)
                                else
                                    painterResource(id = R.drawable.play),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(50.dp))
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

    if (dialogState.isIgnoreBatteryOptimizationDialogVisible) {
        BatteryIgnoreOptimizationDialog(
            onHide = {
                recordAudioViewModel.hideIgnoreBatteryOptimizationDialog(it)
            },
            onConfirm = {
                recordAudioViewModel.openBatterySettings()
                recordAudioViewModel.hideIgnoreBatteryOptimizationDialog(it)
            }
        )
    }
}

@Composable
private fun PulsingStatusDot(
    isRecording: Boolean,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    if (isRecording) {
        val infiniteTransition = rememberInfiniteTransition(label = "audio_rec_pulse")
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
private fun AudioRecordingHudCard(
    recordState: RecordState,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    val isRecording = recordState is RecordState.Recording
    val isPaused = recordState is RecordState.Paused
    val isIdle = recordState is RecordState.Idle

    val borderColor = when {
        isRecording -> Color(0xFFEF4444)
        isPaused -> Color(0xFFF59E0B)
        else -> Color(0xFF263346)
    }

    Box(
        modifier = modifier
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

            Spacer(modifier = Modifier.height(24.dp))

            // Time counter
            Text(
                text = if (isIdle) "00:00:00" else recordState.recordDuration.milliSecondToString(),
                color = if (isIdle) Color(0xFF94A3B8) else Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Audio Waveform Animation
            AudioWaveVisualizer(
                isRecording = isRecording,
                activeColor = if (isRecording) Color(0xFFEF4444) else activeColor
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Audio Specs Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = stringResource(R.string.audio_specs_badge),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isIdle) {
                    stringResource(R.string.audio_ready_hint)
                } else {
                    stringResource(R.string.audio_recording_hint)
                },
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
internal fun requestedPermissions(
    recordAudioViewModel: RecordAudioViewModel,
): ImmutableList<RequestedPermissionModel> {
    val recordAudio by recordAudioViewModel.recordAudioPermissionState.collectAsStateWithLifecycle()
    val postNotification by recordAudioViewModel.postNotificationPermissionState.collectAsStateWithLifecycle()

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) persistentListOf(recordAudio, postNotification)
    else persistentListOf(recordAudio)
}

@Composable
private fun AudioWaveVisualizer(
    isRecording: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Row(
        modifier = modifier.padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barCount = 18
        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount.toFloat()
            val targetHeight = if (isRecording) {
                val wave = kotlin.math.sin(phase + normalizedIndex * 4f)
                val base = 10f + 22f * ((wave + 1f) / 2f)
                base.dp
            } else {
                6.dp
            }
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(targetHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isRecording) activeColor.copy(alpha = 0.85f) else Color(0xFF263346))
            )
        }
    }
}