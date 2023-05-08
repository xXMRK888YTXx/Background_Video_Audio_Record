package com.xxmrk888ytxx.settingsscreen.contracts

import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.flow.Flow

interface ManageCameraConfigContract {

    suspend fun setupCameraType(cameraType: CameraType)

    val currentCameraType: Flow<CameraType>
}