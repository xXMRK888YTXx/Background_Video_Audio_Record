package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
import com.xxmrk888ytxx.settingsscreen.contracts.ManageCameraConfigContract
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraConfig
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraMaxQuality
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraRotation
import com.xxmrk888ytxx.settingsscreen.models.configs.CameraType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType as CameraConfigCameraType
import javax.inject.Inject
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality as CameraConfigMaxQuality
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraRotation as CameraConfigCameraRotation


import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings.DualCameraSettingsManager
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition

class ManageCameraConfigContractImpl @Inject constructor(
    private val cameraConfigManager: CameraConfigManager,
    private val dualCameraSettingsManager: DualCameraSettingsManager
) : ManageCameraConfigContract {

    /**
     * CameraConfigCameraType = com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
     * CameraConfigMaxQuality = com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
     * CameraConfigCameraRotation = com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraRotation
     */

    override suspend fun setupCameraType(cameraType: CameraType) {
        cameraConfigManager.setCameraType(cameraType.toCameraConfigType())
    }

    override suspend fun setupCameraMaxQuality(cameraMaxQuality: CameraMaxQuality) {
        cameraConfigManager.setMaxQuality(cameraMaxQuality.toCameraConfigType())
    }

    override suspend fun setupCameraRotation(cameraRotation: CameraRotation) {
        cameraConfigManager.setCameraRotation(cameraRotation.toCameraConfigCameraRotation())
    }

    override val cameraConfig: Flow<CameraConfig> = cameraConfigManager.config.map {
        CameraConfig(
            cameraType = it.cameraType.toSettingsScreenModel(),
            cameraMaxQuality = it.maxQuality.toSettingsScreenModel(),
            cameraRotation = it.cameraRotation.toSettingsScreenCameraRotation()
        )
    }

    override val dualCameraPrimary: Flow<DualCameraPrimary> = dualCameraSettingsManager.primaryCamera

    override suspend fun setupDualCameraPrimary(primary: DualCameraPrimary) {
        dualCameraSettingsManager.setPrimaryCamera(primary)
    }

    override val pipPosition: Flow<PipPosition> = dualCameraSettingsManager.pipPosition

    override suspend fun setupPipPosition(position: PipPosition) {
        dualCameraSettingsManager.setPipPosition(position)
    }

    override val videoSplitLimit: Flow<com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit> = cameraConfigManager.videoSplitLimit

    override suspend fun setupVideoSplitLimit(limit: com.xxmrk888ytxx.coreandroid.models.VideoSplitLimit) {
        cameraConfigManager.setVideoSplitLimit(limit)
    }

    private fun CameraType.toCameraConfigType() : CameraConfigCameraType {
        return when(this) {
            CameraType.Front -> CameraConfigCameraType.Front
            CameraType.Back -> CameraConfigCameraType.Back
            CameraType.Both -> CameraConfigCameraType.Both
        }
    }

    private fun CameraConfigCameraType.toSettingsScreenModel() : CameraType {
        return when(this) {
            CameraConfigCameraType.Front -> CameraType.Front
            CameraConfigCameraType.Back -> CameraType.Back
            CameraConfigCameraType.Both -> CameraType.Both
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

    private fun CameraRotation.toCameraConfigCameraRotation() : CameraConfigCameraRotation {
        return when(this) {
            CameraRotation.ROTATION_0 -> CameraConfigCameraRotation.ROTATION_0

            CameraRotation.ROTATION_90 -> CameraConfigCameraRotation.ROTATION_90

            CameraRotation.ROTATION_180 -> CameraConfigCameraRotation.ROTATION_180

            CameraRotation.ROTATION_270 -> CameraConfigCameraRotation.ROTATION_270
        }
    }

    private fun CameraConfigCameraRotation.toSettingsScreenCameraRotation() : CameraRotation {
        return when(this) {
            CameraConfigCameraRotation.ROTATION_0 -> CameraRotation.ROTATION_0

            CameraConfigCameraRotation.ROTATION_90 -> CameraRotation.ROTATION_90

            CameraConfigCameraRotation.ROTATION_180 -> CameraRotation.ROTATION_180

            CameraConfigCameraRotation.ROTATION_270 -> CameraRotation.ROTATION_270
        }
    }
}