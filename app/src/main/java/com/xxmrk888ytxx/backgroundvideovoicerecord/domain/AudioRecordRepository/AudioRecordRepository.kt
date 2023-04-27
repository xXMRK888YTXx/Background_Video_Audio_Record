package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.models.AudioModel
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * [Ru]
 * Репозотрий который хранит записанные аудио записи
 */
/**
 * [En]
 * Repository that stores recorded audio recordings
 */
interface AudioRecordRepository {

    suspend fun addFile(file:File)

    suspend fun getFileById(id:Int) : File?

    suspend fun removeFile(id:Int)

    val fileList:Flow<List<AudioModel>>
}