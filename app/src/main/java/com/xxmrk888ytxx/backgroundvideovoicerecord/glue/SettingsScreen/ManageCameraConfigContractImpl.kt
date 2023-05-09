package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraConfig
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType as CameraConfigCameraType
import javax.inject.Inject
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality as CameraConfigMaxQuality


class ManageCameraConfigContractImpl @Inject constructor(
    private val cameraConfigManager: CameraConfigManager
) : ManageCameraConfigContract {

    /**
     * CameraConfigCameraType = com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
     * CameraConfigMaxQuality = com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
     */

    override suspend fun setupCameraType(cameraType: CameraType) {
        cameraConfigManager.setCameraType(cameraType.toCameraConfigType())
    }

    override suspend fun setupCameraMaxQuality(cameraMaxQuality: CameraMaxQuality) {
        cameraConfigManager.setMaxQuality(cameraMaxQuality.toCameraConfigType())
    }

    override val cameraConfig: Flow<CameraConfig> = cameraConfigManager.config.map {
        CameraConfig(
            cameraType = it.cameraType.toSettingsScreenModel(),
            cameraMaxQuality = it.maxQuality.toSettingsScreenModel()
        )
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

    private fun CameraMaxQuality.toCameraConfigType() : CameraConfigMaxQuality {
        return when(this) {
            CameraMaxQuality.SD -> CameraConfigMaxQuality.SD

            CameraMaxQuality.HD -> CameraConfigMaxQuality.HD

            CameraMaxQuality.FHD -> CameraConfigMaxQuality.FHD
        }
    }

    private fun CameraConfigMaxQuality.toSettingsScreenModel() : CameraMaxQuality {
        return when(this) {
            CameraConfigMaxQuality.SD -> CameraMaxQuality.SD
            CameraConfigMaxQuality.HD -> CameraMaxQuality.HD
            CameraConfigMaxQuality.FHD -> CameraMaxQuality.FHD
        }
    }
}