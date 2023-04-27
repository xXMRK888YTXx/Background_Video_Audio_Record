package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.models.AudioModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface AudioRecordRepository {

    suspend fun addFile(file:File)

    suspend fun getFileById(id:Int) : File?

    suspend fun removeFile(id:Int)

    val fileList:Flow<List<AudioModel>>
}