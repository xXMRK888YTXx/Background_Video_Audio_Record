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
}