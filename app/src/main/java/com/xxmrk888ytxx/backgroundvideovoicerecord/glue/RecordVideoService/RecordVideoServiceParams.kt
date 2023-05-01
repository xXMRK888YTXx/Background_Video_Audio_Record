package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.CameraConfigManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.recordvideoservice.RecordVideoParams
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecordVideoServiceParams @Inject constructor (
    override val saveRecordedVideoStrategy: SaveRecordedVideoStrategy,
    private val cameraConfigManager:CameraConfigManager
) : RecordVideoParams {
    override val cameraConfig: Flow<RecordVideoConfig> = cameraConfigManager.config.map {
        RecordVideoConfig(
            cameraType = it.cameraType.toServiceCameraType()
        )
    }

    private fun CameraType.toServiceCameraType() : com.xxmrk888ytxx.recordvideoservice.models.CameraType {
        return when(this) {
            is CameraType.Front -> com.xxmrk888ytxx.recordvideoservice.models.CameraType.Front

            is CameraType.Back -> com.xxmrk888ytxx.recordvideoservice.models.CameraType.Back
        }
    }
}