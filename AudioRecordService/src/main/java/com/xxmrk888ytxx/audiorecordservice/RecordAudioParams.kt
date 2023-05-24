package com.xxmrk888ytxx.audiorecordservice

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioConfig
import kotlinx.coroutines.flow.Flow

/**
 * [Ru]
 * Набор зависимостей необходимых для работы [AudioRecordService]
 */
/**
 * [En]
 * Set of dependencies required for [AudioRecordService]
 */
interface RecordAudioParams {

    val recordAudioConfig: Flow<RecordAudioConfig>

    val saveAudioRecordStrategy:SaveAudioRecordStrategy
}