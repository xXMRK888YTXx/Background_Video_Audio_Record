package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.recordvideoscreen.models.DualCameraSaveOption
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow

interface VideoRecordServiceManager {

    val currentRecordState : Flow<RecordVideoState>

    fun startRecord(cameraTypeOverride: CameraType? = null)

    fun pauseRecord()

    fun resumeRecord()

    fun stopRecord()

    val hasPendingDualRecord: Flow<Boolean>

    suspend fun saveDualRecord(option: DualCameraSaveOption)
}