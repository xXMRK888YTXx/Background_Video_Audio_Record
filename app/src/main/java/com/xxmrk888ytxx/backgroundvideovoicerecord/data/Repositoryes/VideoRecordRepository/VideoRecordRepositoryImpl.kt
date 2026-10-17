package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.VideoFileNameRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.models.VideoNameModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.models.VideoModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class VideoRecordRepositoryImpl @Inject constructor(
    private val context: Context,
    private val videoFileNameRepository: VideoFileNameRepository
) : VideoRecordRepository {

    //file list
    private val _fileList = MutableStateFlow<List<VideoModel>>(emptyList())
    //

    override suspend fun addFileFromRecorded(): File {
        if(fileForRecord.exists()) {
            if (fileForRecord.length() == 0L) {
                fileForRecord.delete()
                if (secondaryFileForRecord.exists()) secondaryFileForRecord.delete()
                throw IllegalStateException("Recorded file is empty (0 bytes)")
            }
            val newFilePath = newFile
            if (!fileForRecord.renameTo(newFilePath)) {
                fileForRecord.copyTo(newFilePath, overwrite = true)
                fileForRecord.delete()
            }
            if (secondaryFileForRecord.exists()) {
                val secondaryTarget = File(videoDir, "${newFilePath.name}_secondary")
                if (!secondaryFileForRecord.renameTo(secondaryTarget)) {
                    secondaryFileForRecord.copyTo(secondaryTarget, overwrite = true)
                    secondaryFileForRecord.delete()
                }
            }
            loadNewFile(newFilePath)
            return newFilePath
        } else {
            throw IllegalStateException("Folder for video records is not created")
        }
    }

    override suspend fun getFileById(id: Long): File? = withContext(Dispatchers.IO) {
        val file = File(videoDir,id.toString())

        return@withContext if(file.exists()) file else null
    }

    override suspend fun getSecondaryFileById(id: Long): File? = withContext(Dispatchers.IO) {
        val file = File(videoDir, "${id}_secondary")

        return@withContext if(file.exists()) file else null
    }

    override fun isDualRecord(id: Long): Boolean {
        return File(videoDir, "${id}_secondary").exists()
    }

    override suspend fun removeFile(id: Long) {
        try {
            File(videoDir,id.toString()).delete()
            File(videoDir, "${id}_secondary").delete()

            deleteFileFromFlow(id)

            videoFileNameRepository.removeName(id)
        }catch (_:Exception) {}
    }

    override val fileList: Flow<List<VideoModel>> = _fileList
        .asStateFlow()
        .onStart {
            loadAllFiles()
        }
        .combine(videoFileNameRepository.videoNamesMapFlow) { list,nameMap ->
            list.map { it.copy(name = nameMap[it.id]?.name) }
        }

    override suspend fun getFileForRecord(): File {
        if(fileForRecord.exists()) {
            addFileFromRecorded()
        }

        return fileForRecord
    }

    override suspend fun getSecondaryFileForRecord(): File {
        if(secondaryFileForRecord.exists()) {
            secondaryFileForRecord.delete()
        }
        return secondaryFileForRecord
    }

    override fun hasPendingDualRecord(): Boolean {
        return fileForRecord.exists() && secondaryFileForRecord.exists()
    }

    override fun getPendingFiles(): Pair<File?, File?> {
        val primary = if (fileForRecord.exists()) fileForRecord else null
        val secondary = if (secondaryFileForRecord.exists()) secondaryFileForRecord else null
        return Pair(primary, secondary)
    }

    override suspend fun addCustomFile(file: File, displayName: String?): File {
        if (file.exists()) {
            val newFilePath = newFile
            if (!file.renameTo(newFilePath)) {
                file.copyTo(newFilePath, overwrite = true)
                file.delete()
            }
            loadNewFile(newFilePath)
            if (displayName != null) {
                val id = newFilePath.fileNameToLong()
                if (id != -1L) {
                    videoFileNameRepository.insertName(VideoNameModel(id, displayName))
                }
            }
            return newFilePath
        } else {
            throw IllegalStateException("File to add does not exist: ${file.absolutePath}")
        }
    }

    override suspend fun cleanUpPendingRecords() {
        if (fileForRecord.exists()) fileForRecord.delete()
        if (secondaryFileForRecord.exists()) secondaryFileForRecord.delete()
    }

    //files

    private val fileForRecord:File
        get() = File(videoDir,"recording")

    private val secondaryFileForRecord: File
        get() = File(videoDir, "recording_secondary")

    private val videoDir : File by lazy {
        ContextWrapper(context).getDir("RecordedVideos", Context.MODE_PRIVATE)
    }

    private val newFile: File
        get() {
            val files = videoDir.listFiles() ?: emptyArray()
            val maxId = files.map { it.fileNameToLong() }.filter { it >= 0 }.maxOrNull() ?: -1L
            var targetId = maxId + 1
            while (File(videoDir, targetId.toString()).exists()) {
                targetId++
            }
            return File(videoDir, targetId.toString())
        }
    //

    //Mappers
    private fun File.fileNameToLong() : Long {
        return try {
            nameWithoutExtension.toLong()
        }catch (e:Exception) {
            -1
        }
    }

    private suspend fun File.toVideoModel() : VideoModel {
        val id = fileNameToLong()
        val duration = getFileDuration(this)
        val createFileTime = getCreateFileTime(this)
        val isDual = File(videoDir, "${id}_secondary").exists()

        return VideoModel(id, duration, createFileTime, isDualRecord = isDual)
    }
    //

    //Info file providers
    private suspend fun getCreateFileTime(file: File): Long {
        return file.lastModified()
    }

    private suspend fun getFileDuration(file:File) : Long {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        return try {
            mediaMetadataRetriever.setDataSource(file.absolutePath)
            mediaMetadataRetriever
                .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            try {
                mediaMetadataRetriever.release()
            } catch (_: Exception) {}
        }
    }
    //

    //Flow control
    private suspend fun loadAllFiles() = withContext(Dispatchers.IO) {
        val files = videoDir.listFiles()?.filterNotNull()?.filter { it.name.toLongOrNull() != null } ?: return@withContext

        _fileList.update { _ ->
            files
                .map { it.toVideoModel() }
                .filter { it.id != -1L }
                .sortedByDescending { it.created }
        }
    }

    private suspend fun loadNewFile(file: File) = withContext(Dispatchers.IO) {
        _fileList.update { it
            .toMutableList()
            .also { list -> list.add(file.toVideoModel()) }
            .sortedByDescending { model ->  model.created }
            .toList()
        }
    }

    private suspend fun deleteFileFromFlow(id:Long) {
        _fileList.update {
            it.toMutableList()
                .filter { fileId -> fileId.id != id }
        }
    }
    //
}