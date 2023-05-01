package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.models.AudioModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordRepository.models.VideoModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface VideoRecordRepository {

    suspend fun addFileFromRecorded()

    suspend fun getFileById(id:Long) : File?

    suspend fun removeFile(id:Long)

    val fileList: Flow<List<VideoModel>>

    suspend fun getFileForRecord() : File
}