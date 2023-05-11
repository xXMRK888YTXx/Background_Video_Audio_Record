package com.xxmrk888ytxx.storagescreen.AudioStorageList

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xxmrk888ytxx.corecompose.Shared.AudioPlayer
import com.xxmrk888ytxx.privatenote.presentation.ActivityLaunchContacts.SingleAccessExternalFileContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioPlayerDialogState
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.PlayerState
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.RenameDialogState
import com.xxmrk888ytxx.storagescreen.ExportLoadingDialog
import com.xxmrk888ytxx.storagescreen.MediaFileItem.MediaFileItem
import com.xxmrk888ytxx.storagescreen.MediaFileItem.models.MediaFileButton
import com.xxmrk888ytxx.storagescreen.R
import com.xxmrk888ytxx.storagescreen.RenameDialog
import com.xxmrk888ytxx.storagescreen.Stub
import kotlinx.collections.immutable.persistentListOf

@SuppressLint("ResourceType")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AudioStorageList(audioStorageListViewModel: AudioStorageListViewModel) {
    val audioFiles by audioStorageListViewModel.audioFiles.collectAsStateWithLifecycle()
    val dialogState by audioStorageListViewModel.dialogState.collectAsStateWithLifecycle()

    val selectExportPathContract = rememberLauncherForActivityResult(
        contract = SingleAccessExternalFileContract(),
        onResult = audioStorageListViewModel::onExportPathSelected
    )

    if(audioFiles.isEmpty()) {
        Stub(text = stringResource(R.string.Audios_missing))
    } else {
        LazyColumn(Modifier.fillMaxSize()) {
            items(audioFiles, key = { it.id }) {
                val mediaButtons = persistentListOf(
                    MediaFileButton(
                        icon = R.drawable.baseline_play_arrow_24,
                        onClick = { audioStorageListViewModel.showAudioDialogState(it) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.baseline_delete_24,
                        onClick = { audioStorageListViewModel.removeAudioFile(it) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.remame,
                        onClick = { audioStorageListViewModel.showRenameDialog(it) }
                    ),
                    MediaFileButton(
                        icon = R.drawable.export,
                        onClick = { audioStorageListViewModel.sendExportPathRequest(
                            audioId = it.id,
                            launcher = selectExportPathContract
                        ) }
                    )
                )

                MediaFileItem(
                    duration = it.duration,
                    created = it.created,
                    name = it.name ?: "",
                    buttons = mediaButtons
                )
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

    if(dialogState.renameDialogState is RenameDialogState.Showed) {

        val state = (dialogState.renameDialogState as RenameDialogState.Showed)
        RenameDialog(
            initialName = state.initialName,
            onDismiss = audioStorageListViewModel::hideRenameDialog,
            onRenamed = { audioStorageListViewModel.changeAudioFileName(state.audioId,it) }
        )
    }

    if(dialogState.isExportLoadingDialogVisible) {
        ExportLoadingDialog()
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
