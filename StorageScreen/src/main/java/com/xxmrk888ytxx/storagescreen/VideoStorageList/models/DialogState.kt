package com.xxmrk888ytxx.storagescreen.VideoStorageList.models

data class DialogState(
    val renameDialogState: RenameDialogState = RenameDialogState.Hidden,
    val isExportLoadingDialogVisible: Boolean = false,
    val dualExportDialogVideoId: Long? = null,
    val dualPlayDialogVideoId: Long? = null,
    val pendingDeleteVideoId: Long? = null
)
