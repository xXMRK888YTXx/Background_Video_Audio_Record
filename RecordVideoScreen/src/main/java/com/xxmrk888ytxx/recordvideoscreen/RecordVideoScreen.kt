package com.xxmrk888ytxx.recordvideoscreen

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
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButton
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButtonHolderWidget
import com.xxmrk888ytxx.corecompose.Shared.RecordStateWidget
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.StyleIconButton
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeShapes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordVideoScreen(
    recordVideoViewModel: RecordVideoViewModel,
) {
    Scaffold(
        Modifier.fillMaxSize(),
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
        ) {
            val isRecord by recordVideoViewModel.isRecord.collectAsStateWithLifecycle()

            val currentWidgetGradientColor by recordVideoViewModel.currentWidgetColor.collectAsStateWithLifecycle()

            val animatedWidgetGradientColor by animateColorAsState(
                targetValue = currentWidgetGradientColor.color,
                animationSpec = tween(
                    durationMillis = 1000
                )
            )

            var alpha by rememberSaveable {
                mutableStateOf(1f)
            }

            val animatedAlpha by animateFloatAsState(
                targetValue = alpha,
                animationSpec = tween(1000)
            )

            LaunchedEffect(key1 = isRecord, block = {
                launch {
                    if (!isRecord) {
                        alpha = 1f
                    }
                    while (isRecord) {
                        recordVideoViewModel.regenerateButtonGradientColor()
                        alpha = if (alpha == 1f) 0f else 1f
                        delay(1100)
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
                        isRecordEnabled = isRecord,
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
                            y = 20.dp
                        )
                    ) {

                        ControlRecordButton(
                            painter = painterResource(id = R.drawable.baseline_visibility_24),
                            background = themeColors.supportControlRecordButtonColor
                        ) {

                        }

                        ControlRecordButton(
                            painter = painterResource(
                                id = if (!isRecord) R.drawable.videocam
                                else R.drawable.baseline_stop_24
                            ),
                            background = themeColors.recordButtonColor
                        ) {
                            if (isRecord) {
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
    }
}