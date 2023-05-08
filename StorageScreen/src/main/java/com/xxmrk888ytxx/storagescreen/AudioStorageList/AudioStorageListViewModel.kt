package com.xxmrk888ytxx.storagescreen.AudioStorageList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ChangeAudioFileNameContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.DeleteAudioFileContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.PlayerFactoryContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts.ProvideFileByAudioId
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioFileModel
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.AudioPlayerDialogState
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.DialogState
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.LockBlockerScreen
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.PlayerState
import com.xxmrk888ytxx.storagescreen.AudioStorageList.models.RenameDialogState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AudioStorageListViewModel @AssistedInject constructor(
    private val provideAudioFilesContract: ProvideAudioFilesContract,
    private val playerFactoryContract: PlayerFactoryContract,
    @Assisted private val lockBlockerScreen: LockBlockerScreen,
    private val provideFileByAudioId: ProvideFileByAudioId,
    private val deleteAudioFileContract: DeleteAudioFileContract,
    private val changeAudioFileNameContract: ChangeAudioFileNameContract
) : ViewModel()  {

    val audioFiles = provideAudioFilesContract.files
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), persistentListOf())

    private val _dialogState = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()

    internal fun showRenameDialog(audio: AudioFileModel) {
        _dialogState.update { it.copy(
            renameDialogState = RenameDialogState.Showed(
                audioId = audio.id,
                initialName = audio.name ?: ""
            )
        ) }
    }

    internal fun hideRenameDialog() {
        _dialogState.update { it.copy(
            renameDialogState = RenameDialogState.Hidden
        ) }
    }

    internal fun changeAudioFileName(audioId:Long,newName:String) {
        hideRenameDialog()

        viewModelScope.launch(Dispatchers.IO) {
            changeAudioFileNameContract.changeFileName(audioId,newName)
        }
    }

    fun showAudioDialogState(audio: AudioFileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val audioFile = provideFileByAudioId.provide(audio.id.toInt()) ?: return@launch
            val player = playerFactoryContract.create(lockBlockerScreen::enable,lockBlockerScreen::cancel)
            player.prepare(audioFile)
            _dialogState.update { it.copy(
                audioPlayerDialogState = AudioPlayerDialogState.Showed(player,audio.duration,audioFile)
            ) }
            player.play()
        }
    }


    fun play() {
        _dialogState.value.audioPlayerDialogState.apply {
            viewModelScope.launch {
                if(this@apply is AudioPlayerDialogState.Showed) {
                    when(player.state.first()) {
                        is PlayerState.Pause -> player.play()

                        is PlayerState.Idle -> {
                            if(player.isPrepare) player.play()
                            else player.prepare(playFile).also { player.play() }
                        }

                        else -> return@launch
                    }
                }
            }
        }
    }

    fun pause() {
        _dialogState.value.audioPlayerDialogState.apply {
            if(this is AudioPlayerDialogState.Showed) {
                player.pause()
            }
        }
    }

    fun seekTo(time:Long) {
        _dialogState.value.audioPlayerDialogState.apply {
            if(this is AudioPlayerDialogState.Showed) {
                player.seekTo(time)

            }
        }
    }

    fun hideAudioPlayerDialog() {
        _dialogState.value.audioPlayerDialogState.apply {
            if(this is AudioPlayerDialogState.Showed) {
                val player = player
                _dialogState.update { it.copy(audioPlayerDialogState = AudioPlayerDialogState.Hidden) }

                player.stop()
                player.destroy()
            }
        }
    }

    fun removeAudioFile(file:AudioFileModel) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAudioFileContract.execute(file.id.toInt())

        }
    }


    @AssistedFactory
    interface Factory {
        fun create(lockBlockerScreen: LockBlockerScreen) : AudioStorageListViewModel
    }
}