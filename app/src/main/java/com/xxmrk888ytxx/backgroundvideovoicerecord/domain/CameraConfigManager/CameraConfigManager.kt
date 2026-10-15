package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraRotation
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
import kotlinx.coroutines.flow.Flow

interface CameraConfigManager {

    val config: Flow<CameraConfig>

    suspend fun setCameraType(cameraType: CameraType)

    suspend fun setMaxQuality(maxQuality: MaxQuality)

    suspend fun setCameraRotation(cameraRotation: CameraRotation)

    val videoSplitLimit: Flow<com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit>

    suspend fun setVideoSplitLimit(splitLimit: com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit)
}