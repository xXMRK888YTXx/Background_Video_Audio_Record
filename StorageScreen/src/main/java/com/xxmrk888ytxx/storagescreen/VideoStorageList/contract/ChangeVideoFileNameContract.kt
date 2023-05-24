package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

interface ChangeVideoFileNameContract {

    suspend fun changeFileName(id:Long,newName:String)
}