package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import kotlinx.coroutines.flow.Flow

interface AudioRecordServiceManager {

    val currentRecordState : Flow<RecordAudioState>

    fun startRecord()

    fun pauseRecord()

    fun resumeRecord()

    fun stopRecord()
}