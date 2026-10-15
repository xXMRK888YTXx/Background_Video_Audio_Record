package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.recordvideoscreen.contract.ManageCameraTypeContract
import com.xxmrk888ytxx.recordvideoscreen.models.CurrentSelectedCameraModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings.DualCameraSettingsManager
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition

class ManageCameraTypeContractImpl @Inject constructor(
    private val cameraConfigManager: CameraConfigManager,
    private val dualCameraSettingsManager: DualCameraSettingsManager
) : ManageCameraTypeContract {
    override val cameraType: Flow<CurrentSelectedCameraModel> = cameraConfigManager.config.map {
        it.cameraType.toCurrentSelectedCamera()
    }

    override suspend fun setCurrentSelectedCamera(currentSelectedCamera: CurrentSelectedCameraModel) {
        cameraConfigManager.setCameraType(currentSelectedCamera.toCameraType())
    }

    override val dualCameraPrimary: Flow<DualCameraPrimary> = dualCameraSettingsManager.primaryCamera

    override val pipPosition: Flow<PipPosition> = dualCameraSettingsManager.pipPosition

    private fun CameraType.toCurrentSelectedCamera() : CurrentSelectedCameraModel {
        return when(this) {
            is CameraType.Front -> CurrentSelectedCameraModel.Front
            is CameraType.Back -> CurrentSelectedCameraModel.Back
            is CameraType.Both -> CurrentSelectedCameraModel.Both
        }
    }

    private fun CurrentSelectedCameraModel.toCameraType() : CameraType {
        return when(this) {
            is CurrentSelectedCameraModel.Back -> CameraType.Back
            is CurrentSelectedCameraModel.Front -> CameraType.Front
            is CurrentSelectedCameraModel.Both -> CameraType.Both
        }
    }
}