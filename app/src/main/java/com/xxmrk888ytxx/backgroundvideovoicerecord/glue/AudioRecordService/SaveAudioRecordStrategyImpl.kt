package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AudioRecordService

import android.content.Context
import android.util.Log
import com.xxmrk888ytxx.audiorecordservice.SaveAudioRecordStrategy
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.ExternalStorageExportManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveAudioRecordStrategyImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository,
    private val externalStorageExportManager: ExternalStorageExportManager
) : SaveAudioRecordStrategy {

    override suspend fun saveRecord() = withContext(Dispatchers.IO) {
        runCatching { audioRecordRepository.addFileFromRecorded() }
            .onSuccess { file -> externalStorageExportManager.exportNewAudioFile(file) }
            .onFailure { Log.e(LOG_TAG,it.stackTraceToString()) }
        return@withContext
    }

    private companion object {
        const val LOG_TAG = "SaveAudioRecordStrategyImpl"
    }
}
