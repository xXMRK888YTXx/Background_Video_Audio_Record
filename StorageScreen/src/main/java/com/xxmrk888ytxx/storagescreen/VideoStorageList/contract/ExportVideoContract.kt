package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

import android.net.Uri
import com.xxmrk888ytxx.coreandroid.models.DualCameraSaveOption

interface ExportVideoContract {

    suspend fun exportVideo(videoId: Long, path: Uri): Result<Unit>

    suspend fun exportDualVideo(videoId: Long, path: Uri, option: DualCameraSaveOption): Result<Unit>

    suspend fun exportSecondaryVideo(videoId: Long, path: Uri): Result<Unit>
}