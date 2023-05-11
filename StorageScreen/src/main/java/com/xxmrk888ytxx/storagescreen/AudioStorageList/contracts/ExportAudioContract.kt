package com.xxmrk888ytxx.storagescreen.AudioStorageList.contracts

import android.net.Uri

interface ExportAudioContract {

    suspend fun exportAudio(audioId:Long,outputPath:Uri) : Result<Unit>
}