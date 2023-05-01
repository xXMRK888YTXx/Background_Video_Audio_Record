package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

interface DeleteVideoFileContract {

    suspend fun removeFile(id:Long)
}