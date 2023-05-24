package com.xxmrk888ytxx.storagescreen.VideoStorageList.models

data class VideoFileModel(
    val id:Long,
    val duration:Long,
    val created:Long,
    val name:String? = null
)
