package com.xxmrk888ytxx.recordvideoservice

import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoConfig
import kotlinx.coroutines.flow.Flow

interface RecordVideoParams {

    val saveRecordedVideoStrategy:SaveRecordedVideoStrategy

    val cameraConfig: Flow<RecordVideoConfig>
}