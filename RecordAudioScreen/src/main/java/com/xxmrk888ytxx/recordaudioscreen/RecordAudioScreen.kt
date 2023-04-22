package com.xxmrk888ytxx.recordaudioscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.Shared.ControlRecordButtonHolderWidget
import com.xxmrk888ytxx.corecompose.Shared.RecordStateWidget
import com.xxmrk888ytxx.corecompose.Shared.StyleIcon
import com.xxmrk888ytxx.corecompose.Shared.StyleIconButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RecordAudioScreen(
    recordAudioViewModel: RecordAudioViewModel,
) {

    val isRecord = false

    val currentWidgetGradientColor by recordAudioViewModel.currentWidgetColor.collectAsStateWithLifecycle()

    val animatedWidgetGradientColor by animateColorAsState(
        targetValue = currentWidgetGradientColor.color1,
        animationSpec = tween(
            durationMillis = 1000
        )
    )

    LaunchedEffect(key1 = isRecord, block = {
        launch {
            while (isRecord) {
                delay(1100)
                recordAudioViewModel.regenerateButtonGradientColor()
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
                .verticalScroll(rememberScrollState())
            ,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RecordStateWidget(
                isRecordEnabled = isRecord,
                modifier = Modifier
                    .fillMaxWidth(),
                borderWhenRecordEnabled = animatedWidgetGradientColor
            )

            ControlRecordButtonHolderWidget(
                modifier = Modifier.offset(
                    y = 20.dp
                )
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Red
                    ),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    StyleIconButton(
                        painter = painterResource(id = R.drawable.baseline_fiber_manual_record_24),
                        modifier = Modifier.size(40.dp)
                            .clip(RoundedCornerShape(100))
                    ) {}
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