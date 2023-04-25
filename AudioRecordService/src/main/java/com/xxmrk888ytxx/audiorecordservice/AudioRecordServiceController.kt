package com.xxmrk888ytxx.audiorecordservice

import kotlinx.coroutines.flow.Flow

interface AudioRecordServiceController {

    fun startRecord()

    fun pauseRecord()

    fun resumeRecord()

    fun stopRecord()

    val currentState: Flow<RecordAudioState>
}