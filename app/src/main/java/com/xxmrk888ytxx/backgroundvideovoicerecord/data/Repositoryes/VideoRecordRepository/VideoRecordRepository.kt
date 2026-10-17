package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.models.AudioModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.models.VideoModel
import kotlinx.coroutines.flow.Flow
import java.io.File

interface VideoRecordRepository {

    @Throws(IllegalStateException::class)
    suspend fun addFileFromRecorded(): File

    suspend fun getFileById(id: Long): File?

    suspend fun getSecondaryFileById(id: Long): File?

    fun isDualRecord(id: Long): Boolean

    suspend fun removeFile(id: Long)

    val fileList: Flow<List<VideoModel>>

    suspend fun getFileForRecord(): File

    suspend fun getSecondaryFileForRecord(): File

    fun hasPendingDualRecord(): Boolean

    suspend fun addCustomFile(file: File, displayName: String? = null): File

    fun getPendingFiles(): Pair<File?, File?>

    suspend fun cleanUpPendingRecords()
}