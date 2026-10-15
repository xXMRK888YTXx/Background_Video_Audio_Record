package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings

import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import kotlinx.coroutines.flow.Flow

interface DualCameraSettingsManager {
    val primaryCamera: Flow<DualCameraPrimary>
    val pipPosition: Flow<PipPosition>

    suspend fun setPrimaryCamera(primary: DualCameraPrimary)
    suspend fun setPipPosition(position: PipPosition)
}
