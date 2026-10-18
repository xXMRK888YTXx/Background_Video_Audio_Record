package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.RecordVideoService

import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.ExternalStorageExportManager
import com.xxmrk888ytxx.recordvideoservice.SaveRecordedVideoStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class SaveRecordedVideoStrategyImpl @Inject constructor(
    private val videoRecordRepository: VideoRecordRepository,
    private val externalStorageExportManager: ExternalStorageExportManager
) : SaveRecordedVideoStrategy {

    override suspend fun saveRecord() = withContext(Dispatchers.IO) {
        runCatching { videoRecordRepository.addFileFromRecorded() }
            .onSuccess { file ->
                externalStorageExportManager.exportNewVideoFile(file)
                val secondaryFile = file.name.toLongOrNull()?.let { id ->
                    videoRecordRepository.getSecondaryFileById(id)
                } ?: File(file.parentFile, "${file.name}_secondary")
                if (secondaryFile.exists()) {
                    externalStorageExportManager.exportNewVideoFile(secondaryFile)
                }
            }
            .onFailure { Log.e(LOG_TAG, it.stackTraceToString()) }
        return@withContext
    }

    private companion object {
        const val LOG_TAG = "SaveRecordedVideoStrategyImpl"
    }
}