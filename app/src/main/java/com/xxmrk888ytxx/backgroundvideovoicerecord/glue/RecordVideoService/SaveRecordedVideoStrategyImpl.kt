package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import javax.inject.Inject

class SaveRecordedVideoStrategyImpl @Inject constructor(
    private val videoRecordRepository: VideoRecordRepository
) : SaveRecordedVideoStrategy {

    override suspend fun saveRecord() {
        videoRecordRepository.addFileFromRecorded()
    }
}