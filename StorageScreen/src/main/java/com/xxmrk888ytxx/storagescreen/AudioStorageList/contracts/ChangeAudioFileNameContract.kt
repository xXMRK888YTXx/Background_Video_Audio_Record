package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

interface ChangeAudioFileNameContract {

    suspend fun changeFileName(id:Long,newName:String)
}