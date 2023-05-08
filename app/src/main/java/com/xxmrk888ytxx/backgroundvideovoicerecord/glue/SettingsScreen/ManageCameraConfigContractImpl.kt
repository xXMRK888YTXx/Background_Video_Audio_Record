package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType as CameraConfigCameraType
import javax.inject.Inject


class ManageCameraConfigContractImpl @Inject constructor(
    private val cameraConfigManager: CameraConfigManager
) : ManageCameraConfigContract {

    /**
     * CameraConfigCameraType = com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
     */

    override suspend fun setupCameraType(cameraType: CameraType) {
        cameraConfigManager.setCameraType(cameraType.toCameraConfigType())
    }

    override val currentCameraType: Flow<CameraType> = cameraConfigManager.config.map {
        it.cameraType.toSettingsScreenModel()
    }

    private fun CameraType.toCameraConfigType() : CameraConfigCameraType {
        return when(this) {
            CameraType.Front -> CameraConfigCameraType.Front

            CameraType.Back -> CameraConfigCameraType.Back
        }
    }

    private fun CameraConfigCameraType.toSettingsScreenModel() : CameraType {
        return when(this) {
            CameraConfigCameraType.Front -> CameraType.Front

            CameraConfigCameraType.Back -> CameraType.Back
        }
    }
}