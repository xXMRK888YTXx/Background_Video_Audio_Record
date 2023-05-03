package com.xxmrk888ytxx.storagescreen.VideoStorageList.models

sealed class RenameDialogState {

    object Hidden : RenameDialogState()

    data class Showed(val videoId:Long,val initialName:String) : RenameDialogState()
}