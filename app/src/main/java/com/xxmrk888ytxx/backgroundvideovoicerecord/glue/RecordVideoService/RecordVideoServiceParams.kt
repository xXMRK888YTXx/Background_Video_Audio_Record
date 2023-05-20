package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraRotation
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoForegroundNotificationConfig.VideoForegroundNotificationConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.models.ForegroundNotificationConfig
import com.xxmrk888ytxx.recordvideoservice.RecordVideoParams
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.xxmrk888ytxx.recordvideoservice.models.CameraType as ServiceCameraType
import com.xxmrk888ytxx.recordvideoservice.models.MaxQuality as ServiceMaxQuality
import com.xxmrk888ytxx.recordvideoservice.models.CameraRotation as ServiceCameraRotation
import com.xxmrk888ytxx.recordvideoservice.models.ForegroundNotificationType as VideoForegroundNotificationType

/**
 * ServiceCameraType = com.xxmrk888ytxx.recordvideoservice.models.CameraType
 * ServiceMaxQuality = com.xxmrk888ytxx.recordvideoservice.models.MaxQuality
 * ServiceCameraRotation = com.xxmrk888ytxx.recordvideoservice.models.CameraRotation
 * VideoForegroundNotificationType = com.xxmrk888ytxx.recordvideoservice.models.ForegroundNotificationType
 */

class RecordVideoServiceParams @Inject constructor(
    override val saveRecordedVideoStrategy: SaveRecordedVideoStrategy,
    private val cameraConfigManager: CameraConfigManager,
    private val videoForegroundNotificationConfig: VideoForegroundNotificationConfig,
) : RecordVideoParams {


    override val cameraConfig: Flow<RecordVideoConfig> = combine(
        cameraConfigManager.config, videoForegroundNotificationConfig.config
    ) { cameraConfig, videoForegroundNotificationConfig ->

        RecordVideoConfig(
            cameraType = cameraConfig.cameraType.toServiceCameraType(),
            maxQuality = cameraConfig.maxQuality.toServiceMaxQuality(),
            cameraRotation = cameraConfig.cameraRotation.toServiceCameraRotation(),
            foregroundNotificationType = videoForegroundNotificationConfig.toServiceForegroundType(),
        )
    }

    private fun CameraType.toServiceCameraType(): ServiceCameraType {
        return when (this) {
            is CameraType.Front -> com.xxmrk888ytxx.recordvideoservice.models.CameraType.Front

            is CameraType.Back -> com.xxmrk888ytxx.recordvideoservice.models.CameraType.Back
        }
    }

    private fun MaxQuality.toServiceMaxQuality(): ServiceMaxQuality {
        return when (this) {
            MaxQuality.SD -> ServiceMaxQuality.SD

            MaxQuality.HD -> ServiceMaxQuality.HD

            MaxQuality.FHD -> ServiceMaxQuality.FHD
        }
    }

    private fun CameraRotation.toServiceCameraRotation(): ServiceCameraRotation {
        return when (this) {
            CameraRotation.ROTATION_0 -> ServiceCameraRotation.ROTATION_0

            CameraRotation.ROTATION_90 -> ServiceCameraRotation.ROTATION_90

            CameraRotation.ROTATION_180 -> ServiceCameraRotation.ROTATION_180

            CameraRotation.ROTATION_270 -> ServiceCameraRotation.ROTATION_270
        }
    }

    private fun ForegroundNotificationConfig.toServiceForegroundType(): VideoForegroundNotificationType {
        return when (this) {
            is ForegroundNotificationConfig.CustomNotification -> VideoForegroundNotificationType.CustomNotification(
               isPauseResumeButtonActive,isStopRecordButtonEnabled,title,text
            )

            is ForegroundNotificationConfig.ViewRecordStateType -> VideoForegroundNotificationType.ViewRecordStateType(
                isPauseResumeButtonActive,isStopRecordButtonEnabled
            )
        }
    }
}