package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.recordvideoscreen.contract.ManageCameraTypeContract
import com.xxmrk888ytxx.recordvideoscreen.models.CurrentSelectedCameraModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageCameraTypeContractImpl @Inject constructor(
    private val cameraConfigManager: CameraConfigManager
) : ManageCameraTypeContract {
    override val cameraType: Flow<CurrentSelectedCameraModel> = cameraConfigManager.config.map {
        it.cameraType.toCurrentSelectedCamera()
    }

    override suspend fun setCurrentSelectedCamera(currentSelectedCamera: CurrentSelectedCameraModel) {
        cameraConfigManager.setCameraType(currentSelectedCamera.toCameraType())
    }

    private fun CameraType.toCurrentSelectedCamera() : CurrentSelectedCameraModel {
        return when(this) {
            is CameraType.Front -> CurrentSelectedCameraModel.Front

            is CameraType.Back -> CurrentSelectedCameraModel.Back
        }
    }

    private fun CurrentSelectedCameraModel.toCameraType() : CameraType {
        return when(this) {
            is CurrentSelectedCameraModel.Back -> CameraType.Back

            is CurrentSelectedCameraModel.Front -> CameraType.Front
        }
    }
}