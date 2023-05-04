package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService

import android.content.Context
import com.xxmrk888ytxx.audiorecordservice.SaveAudioRecordStrategy
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import javax.inject.Inject

class SaveAudioRecordStrategyImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository
) : SaveAudioRecordStrategy {

    override suspend fun saveRecord() {
        audioRecordRepository.addFileFromRecorded()
    }
}
