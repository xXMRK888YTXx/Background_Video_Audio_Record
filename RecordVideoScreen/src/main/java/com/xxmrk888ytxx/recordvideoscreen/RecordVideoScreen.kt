package com.xxmrk888ytxx.recordvideoscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.LocalTheme
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButton
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButtonHolderWidget
import com.xxmrk888ytxx.corecompose.Shared.RecordStateWidget
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.StyleIconButton
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeShapes
import com.xxmrk888ytxx.recordvideoscreen.models.RecordState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordVideoScreen(
    recordVideoViewModel: RecordVideoViewModel,
) {
    val recordState by recordVideoViewModel.currentRecordState.collectAsStateWithLifecycle()

    val currentWidgetGradientColor by recordVideoViewModel.currentWidgetColor.collectAsStateWithLifecycle()

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
                recordVideoViewModel.regenerateButtonGradientColor()
                alpha = if (alpha == 1f) 0f else 1f
                delay((themeValues.animationDuration + themeValues.additionalAnimationDuration).toLong())
            }
        }
    })

    Scaffold(
        Modifier
            .fillMaxSize(),
        backgroundColor = Color.Transparent
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecordStateWidget(
                isRecordEnabled = recordState !is RecordState.Idle,
                recordTime = recordState.currentDuration,
                modifier = Modifier
                    .fillMaxWidth(),
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

            ControlRecordButtonHolderWidget(
                modifier = Modifier.offset(
                    y = themeDimensions.controlRecordButtonHolderWidgetOffset
                )
            ) {

                ControlRecordButton(
                    painter = painterResource(id = R.drawable.baseline_visibility_24),
                    background = themeColors.supportControlRecordButtonColor
                ) {

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