package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import java.io.File

interface ProvideFileByAudioId {

    suspend fun provide(id:Int) : File?
}