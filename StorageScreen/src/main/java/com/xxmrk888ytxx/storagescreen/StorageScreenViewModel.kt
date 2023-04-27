package com.xxmrk888ytxx.storagescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xxmrk888ytxx.storagescreen.contracts.PlayerFactoryContract
import com.xxmrk888ytxx.storagescreen.contracts.ProvideAudioFilesContract
import com.xxmrk888ytxx.storagescreen.contracts.ProvideFileByAudioId
import com.xxmrk888ytxx.storagescreen.models.AudioFileModel
import com.xxmrk888ytxx.storagescreen.models.AudioPlayerDialogState
import com.xxmrk888ytxx.storagescreen.models.DialogState
import com.xxmrk888ytxx.storagescreen.models.LockBlockerScreen
import com.xxmrk888ytxx.storagescreen.models.Player
import com.xxmrk888ytxx.storagescreen.models.PlayerState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

class StorageScreenViewModel @AssistedInject constructor(
    private val provideAudioFilesContract: ProvideAudioFilesContract,
    private val playerFactoryContract: PlayerFactoryContract,
    @Assisted private val lockBlockerScreen: LockBlockerScreen,
    private val provideFileByAudioId: ProvideFileByAudioId
) : ViewModel()  {

    val audioFiles = provideAudioFilesContract.files
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _dialogState = MutableStateFlow(DialogState())

    internal val dialogState = _dialogState.asStateFlow()

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


    @AssistedFactory
    interface Factory {
        fun create(lockBlockerScreen: LockBlockerScreen) : StorageScreenViewModel
    }
}