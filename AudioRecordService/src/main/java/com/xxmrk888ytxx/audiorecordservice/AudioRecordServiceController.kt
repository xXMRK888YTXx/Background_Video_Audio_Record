package com.xxmrk888ytxx.audiorecordservice

import kotlinx.coroutines.flow.Flow

interface AudioRecordServiceController {

    fun startRecord()

    fun pauseRecord()

    fun stopRecord()

    val currentState: Flow<RecordAudioState>
}