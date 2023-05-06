package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.DelayStartRecordManager.models.DelayRecordTask
import kotlinx.coroutines.flow.Flow

interface DelayStartRecordManager {

    suspend fun setDelayAudioRecord(time:Long)

    suspend fun setDelayVideoRecord(time: Long)

    suspend fun cancelDelayTask()

    suspend fun restoreDelayTask()

    val currentTask : Flow<DelayRecordTask?>
}