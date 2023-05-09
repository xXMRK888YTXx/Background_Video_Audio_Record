package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
import com.xxmrk888ytxx.recordvideoservice.RecordVideoParams
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.xxmrk888ytxx.recordvideoservice.models.CameraType as ServiceCameraType
import com.xxmrk888ytxx.recordvideoservice.models.MaxQuality as ServiceMaxQuality

/**
 * ServiceCameraType = com.xxmrk888ytxx.recordvideoservice.models.CameraType
 * ServiceMaxQuality = com.xxmrk888ytxx.recordvideoservice.models.MaxQuality
 */

class RecordVideoServiceParams @Inject constructor (
    override val saveRecordedVideoStrategy: SaveRecordedVideoStrategy,
    private val cameraConfigManager:CameraConfigManager
) : RecordVideoParams {
    override val cameraConfig: Flow<RecordVideoConfig> = cameraConfigManager.config.map {
        RecordVideoConfig(
            cameraType = it.cameraType.toServiceCameraType(),
            maxQuality = it.maxQuality.toServiceMaxQuality()
        )
    }

    private fun CameraType.toServiceCameraType() : ServiceCameraType {
        return when(this) {
            is CameraType.Front -> com.xxmrk888ytxx.recordvideoservice.models.CameraType.Front

            is CameraType.Back -> com.xxmrk888ytxx.recordvideoservice.models.CameraType.Back
        }
    }

    private fun MaxQuality.toServiceMaxQuality() : ServiceMaxQuality {
        return when(this) {
            MaxQuality.SD -> ServiceMaxQuality.SD

            MaxQuality.HD -> ServiceMaxQuality.HD

            MaxQuality.FHD -> ServiceMaxQuality.FHD
        }
    }
}