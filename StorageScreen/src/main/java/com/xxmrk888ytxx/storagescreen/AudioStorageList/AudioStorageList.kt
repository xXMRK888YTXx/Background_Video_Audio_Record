package com.xxmrk888ytxx.storagescreen.AudioStorageList

import android.annotation.SuppressLint
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
import androidx.compose.runtime.remember
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
import com.xxmrk888ytxx.storagescreen.MediaFileItem.MediaFileItem
import com.xxmrk888ytxx.storagescreen.MediaFileItem.models.MediaFileButton
import com.xxmrk888ytxx.storagescreen.R

@SuppressLint("ResourceType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioStorageList(audioStorageListViewModel: AudioStorageListViewModel) {
    val audioFiles by audioStorageListViewModel.audioFiles.collectAsStateWithLifecycle()
    val dialogState by audioStorageListViewModel.dialogState.collectAsStateWithLifecycle()

    LazyColumn(Modifier.fillMaxSize()) {
        items(audioFiles, key = { it.id }) {
            val mediaButtons = remember {
                listOf(
                    MediaFileButton(
                        icon = R.drawable.baseline_play_arrow_24,
                        onClick = { audioStorageListViewModel.showAudioDialogState(it) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.baseline_delete_24,
                        onClick = { audioStorageListViewModel.removeAudioFile(it) }
                    )
                )
            }

            MediaFileItem(
                duration = it.duration,
                created = it.created,
                buttons = mediaButtons
            )
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
