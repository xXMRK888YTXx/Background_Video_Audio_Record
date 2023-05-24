package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

import android.net.Uri

interface ExportVideoContract {

    suspend fun exportVideo(videoId:Long,path:Uri) : Result<Unit>
}