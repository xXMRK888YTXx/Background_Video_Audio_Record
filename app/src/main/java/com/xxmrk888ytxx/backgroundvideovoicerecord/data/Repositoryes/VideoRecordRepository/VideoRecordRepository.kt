package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.models.AudioModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.models.VideoModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface VideoRecordRepository {

    suspend fun addFileFromRecorded()

    suspend fun getFileById(id:Long) : File?

    suspend fun removeFile(id:Long)

    val fileList: Flow<List<VideoModel>>

    suspend fun getFileForRecord() : File
}