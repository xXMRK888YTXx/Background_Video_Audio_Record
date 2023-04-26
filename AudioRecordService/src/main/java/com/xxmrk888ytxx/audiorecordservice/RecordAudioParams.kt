package com.xxmrk888ytxx.audiorecordservice

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioConfig

interface RecordAudioParams {

    val recordAudioConfig: RecordAudioConfig

    val saveAudioRecordStrategy:SaveAudioRecordStrategy
}