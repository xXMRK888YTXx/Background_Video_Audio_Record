package com.xxmrk888ytxx.audiorecordservice

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioConfig

/**
 * [Ru]
 * Набор зависимостей необходимых для работы [AudioRecordService]
 */
/**
 * [En]
 * Set of dependencies required for [AudioRecordService]
 */
interface RecordAudioParams {

    val recordAudioConfig: RecordAudioConfig

    val saveAudioRecordStrategy:SaveAudioRecordStrategy
}