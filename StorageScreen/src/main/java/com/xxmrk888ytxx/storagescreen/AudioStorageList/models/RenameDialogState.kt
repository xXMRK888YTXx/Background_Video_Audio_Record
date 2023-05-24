package com.xxmrk888ytxx.storagescreen.AudioStorageList.models

sealed class RenameDialogState {

    object Hidden : RenameDialogState()

    data class Showed(val audioId:Long,val initialName:String) : RenameDialogState()
}
