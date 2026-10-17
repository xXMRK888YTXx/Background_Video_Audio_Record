package com.xxmrk888ytxx.settingsscreen.contracts

import com.xxmrk888ytxx.settingsscreen.models.configs.CameraConfig
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraRotation
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.flow.Flow

interface ManageCameraConfigContract {

    suspend fun setupCameraType(cameraType: CameraType)

    suspend fun setupCameraMaxQuality(cameraMaxQuality: CameraMaxQuality)

    suspend fun setupCameraRotation(cameraRotation: CameraRotation)

    val cameraConfig: Flow<CameraConfig>

    val dualCameraPrimary: Flow<com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary>

    suspend fun setupDualCameraPrimary(primary: com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary)

    val pipPosition: Flow<com.xxmrk888ytxx.coreandroid.models.PipPosition>

    suspend fun setupPipPosition(position: com.xxmrk888ytxx.coreandroid.models.PipPosition)

    val videoSplitLimit: Flow<com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit>

    suspend fun setupVideoSplitLimit(limit: com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit)
}