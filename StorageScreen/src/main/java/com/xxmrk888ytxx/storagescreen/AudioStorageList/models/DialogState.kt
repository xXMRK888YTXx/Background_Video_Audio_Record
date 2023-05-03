package com.xxmrk888ytxx.storagescreen.AudioStorageList.models

internal data class DialogState(
    val audioPlayerDialogState: AudioPlayerDialogState = AudioPlayerDialogState.Hidden,
    val renameDialogState:RenameDialogState = RenameDialogState.Hidden
)