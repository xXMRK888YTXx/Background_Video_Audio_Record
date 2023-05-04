package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.models.AudioModel
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

    suspend fun addFileFromRecorded()

    suspend fun getFileById(id:Int) : File?

    suspend fun removeFile(id:Int)

    val fileList:Flow<List<AudioModel>>

    suspend fun getFileForRecord() : File
}