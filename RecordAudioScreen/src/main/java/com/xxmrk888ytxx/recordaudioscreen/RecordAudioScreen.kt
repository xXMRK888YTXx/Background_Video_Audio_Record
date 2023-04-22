package com.xxmrk888ytxx.recordaudioscreen

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
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordAudioScreen(
    recordAudioViewModel: RecordAudioViewModel,
) {

    val isRecord by recordAudioViewModel.isRecord.collectAsStateWithLifecycle()

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

    LaunchedEffect(key1 = isRecord, block = {
        launch {
            if(!isRecord) {
                alpha = 1f
            }
            while (isRecord) {
                recordAudioViewModel.regenerateButtonGradientColor()
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
                isRecordEnabled = isRecord,
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

            ControlRecordButtonHolderWidget(
                modifier = Modifier.offset(
                    y = themeDimensions.controlRecordButtonHolderWidgetOffset
                )
            ) {
                ControlRecordButton(
                    painter = painterResource(
                        id = if (!isRecord) R.drawable.baseline_mic_24
                        else R.drawable.baseline_stop_24
                    ),
                    background = themeColors.recordButtonColor
                ) {
                    if (isRecord) {
                        recordAudioViewModel.stopRecord()
                    } else {
                        recordAudioViewModel.startRecord()
                    }
                }
            }


        }
    }

}

@Composable
@Preview
fun prev() {
    //RecordWidget()
}