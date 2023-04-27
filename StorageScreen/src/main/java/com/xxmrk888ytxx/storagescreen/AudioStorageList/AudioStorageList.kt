package com.xxmrk888ytxx.storagescreen.AudioStorageList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.coreandroid.milliSecondToString
import com.xxmrk888ytxx.coreandroid.toDateString
import com.xxmrk888ytxx.coreandroid.toTimeString
import com.xxmrk888ytxx.corecompose.Shared.AudioPlayer
import com.xxmrk888ytxx.corecompose.Shared.StyleCard
import com.xxmrk888ytxx.corecompose.Shared.StyleIconButton
import com.xxmrk888ytxx.corecompose.themeColors
import com.xxmrk888ytxx.corecompose.themeDimensions
import com.xxmrk888ytxx.corecompose.themeTypography
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioPlayerDialogState
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.PlayerState
import com.xxmrk888ytxx.storagescreen.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioStorageList(audioStorageListViewModel: AudioStorageListViewModel) {
    val context = LocalContext.current

    val audioFiles by audioStorageListViewModel.audioFiles.collectAsStateWithLifecycle()
    val dialogState by audioStorageListViewModel.dialogState.collectAsStateWithLifecycle()

    LazyColumn(Modifier.fillMaxSize()) {
        items(audioFiles) {
            StyleCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(themeDimensions.cardOutPaddings),
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(themeDimensions.cardInPaddings)
                ) {
                    Text(
                        text = "${it.created.toDateString(context)}, ${it.created.toTimeString()}",
                        style = themeTypography.head,
                        color = themeColors.primaryFontColor
                    )

                    Text(
                        text = it.duration.milliSecondToString(),
                        color = themeColors.secondFontColor,
                        style = themeTypography.body
                    )

                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StyleIconButton(
                            painter = painterResource(
                                R.drawable.baseline_play_arrow_24
                            )
                        ) {
                            audioStorageListViewModel.showAudioDialogState(it)
                        }

                        StyleIconButton(
                            painter = painterResource(
                                R.drawable.baseline_delete_24
                            )
                        ) {
                            audioStorageListViewModel.removeAudioFile(it)
                        }
                    }
                }
            }
        }

    }

    if (dialogState.audioPlayerDialogState is AudioPlayerDialogState.Showed) {
        val playerState by (dialogState.audioPlayerDialogState as AudioPlayerDialogState.Showed)
            .player.state.collectAsStateWithLifecycle(initialValue = PlayerState.Idle)
        val maxDuration =
            (dialogState.audioPlayerDialogState as AudioPlayerDialogState.Showed).maxDuration

        AudioPlayerDialog(
            audioStorageListViewModel = audioStorageListViewModel,
            playerState = playerState,
            maxDuration = maxDuration
        )
    }
}

@Composable
fun AudioPlayerDialog(
    audioStorageListViewModel: AudioStorageListViewModel,
    playerState: PlayerState,
    maxDuration: Long,
) {

    val isPlay = playerState is PlayerState.Play

    val fastStep = 5000

    AudioPlayer(
        currentDuration = playerState.currentDuration,
        maxDuration = maxDuration,
        isPlay = isPlay,
        onHidePlayer = audioStorageListViewModel::hideAudioPlayerDialog,
        onSeekTo = audioStorageListViewModel::seekTo,
        onChangePlayState = {
            if (isPlay) {
                audioStorageListViewModel.pause()
            } else {
                audioStorageListViewModel.play()
            }
        },
        onFastRewind = { audioStorageListViewModel.seekTo(playerState.currentDuration - fastStep) },
        onFastForward = { audioStorageListViewModel.seekTo(playerState.currentDuration + fastStep) }
    )

}
