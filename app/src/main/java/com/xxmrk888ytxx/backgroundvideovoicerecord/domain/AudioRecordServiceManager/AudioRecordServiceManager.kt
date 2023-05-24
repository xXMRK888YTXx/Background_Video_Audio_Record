package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager

import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import kotlinx.coroutines.flow.Flow

/**
 * [Ru]
 * Менежер для управления и связью с сервисом для записи аудио
 */

/**
 * [En]
 * Manager for controlling and communicating with the audio recording service
 */
interface AudioRecordServiceManager {

    val currentRecordState : Flow<RecordAudioState>

    fun startRecord()

    fun pauseRecord()

    fun resumeRecord()

    fun stopRecord()
}