package com.xxmrk888ytxx.storagescreen.AudioStorageList.models

data class AudioFileModel(
    val id:Long,
    val duration:Long,
    val created:Long,
    val name:String? = null
)
