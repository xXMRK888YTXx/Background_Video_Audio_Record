package com.xxmrk888ytxx.storagescreen.VideoStorageList.models

data class DialogState(
    val renameDialogState: RenameDialogState = RenameDialogState.Hidden,
    val isExportLoadingDialogVisible:Boolean = false
)
