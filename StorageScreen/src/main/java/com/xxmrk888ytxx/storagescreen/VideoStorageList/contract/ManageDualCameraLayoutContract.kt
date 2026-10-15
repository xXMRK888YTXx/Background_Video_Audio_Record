package com.xxmrk888ytxx.storagescreen.VideoStorageList.contract

import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import kotlinx.coroutines.flow.Flow

interface ManageDualCameraLayoutContract {
    val primaryCamera: Flow<DualCameraPrimary>
    val pipPosition: Flow<PipPosition>
    suspend fun setPrimaryCamera(primary: DualCameraPrimary)
    suspend fun setPipPosition(position: PipPosition)
}
