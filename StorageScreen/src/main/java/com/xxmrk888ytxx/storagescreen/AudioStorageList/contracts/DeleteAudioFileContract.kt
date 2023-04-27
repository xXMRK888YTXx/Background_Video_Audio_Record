package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

interface DeleteAudioFileContract {

    suspend fun execute(id:Int)
}