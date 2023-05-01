package com.xxmrk888ytxx.recordvideoscreen.contract

import com.xxmrk888ytxx.recordvideoscreen.models.CurrentSelectedCameraModel
import kotlinx.coroutines.flow.Flow

interface ManageCameraTypeContract {

    val cameraType: Flow<CurrentSelectedCameraModel>

    suspend fun setCurrentSelectedCamera(currentSelectedCamera:CurrentSelectedCameraModel)
}