package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager

import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.flow.Flow

interface VideoRecordServiceManager {

    val currentRecordState : Flow<RecordVideoState>

    fun startRecord()

    fun pauseRecord()

    fun resumeRecord()

    fun stopRecord()
}