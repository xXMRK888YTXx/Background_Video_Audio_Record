package com.xxmrk888ytxx.recordvideoscreen.contract

import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.recordvideoscreen.models.CurrentSelectedCameraModel
import kotlinx.coroutines.flow.Flow

interface ManageCameraTypeContract {

    val cameraType: Flow<CurrentSelectedCameraModel>

    suspend fun setCurrentSelectedCamera(currentSelectedCamera:CurrentSelectedCameraModel)

    val dualCameraPrimary: Flow<DualCameraPrimary>

    val pipPosition: Flow<PipPosition>
}