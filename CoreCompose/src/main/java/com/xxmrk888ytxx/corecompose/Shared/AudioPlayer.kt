package com.xxmrk888ytxx.corecompose.Shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.corecompose.R
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.themeTypography
import kotlinx.coroutines.flow.Flow
import java.time.Duration

@Composable
fun AudioPlayer(
    currentDuration: Long,
    maxDuration:Long,
    isPlay:Boolean,
    onHidePlayer:() -> Unit,
    onSeekTo:(Long) -> Unit,
    onChangePlayState:() -> Unit,
    onFastRewind:() -> Unit,
    onFastForward:() -> Unit
) {
    Dialog(onDismissRequest = onHidePlayer) {
        StyleCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterVertically)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentDuration.milliSecondToString(),
                        style = themeTypography.playerText
                    )

                    Slider(
                        value = currentDuration.toFloat(),
                        onValueChange = { onSeekTo(it.toLong()) },
                        valueRange = 0f..maxDuration.toFloat(),
                        colors = SliderDefaults.colors()
                    )

                    Text(
                        text = maxDuration.milliSecondToString(),
                        style = themeTypography.playerText
                    )
                }

                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp,Alignment.CenterHorizontally)
                ) {
                    StyleIconButton(
                        painter = painterResource(id = R.drawable.baseline_fast_rewind_24),
                        onClick = onFastRewind
                    )

                    StyleIconButton(
                        painter = if(isPlay) painterResource(R.drawable.baseline_pause_24)
                        else painterResource(id = R.drawable.baseline_play_arrow_24),
                        onClick = onChangePlayState
                    )

                    StyleIconButton(
                        painter = painterResource(id = R.drawable.baseline_fast_forward_24),
                        onClick = onFastForward
                    )
                }
            }
        }
    }
}