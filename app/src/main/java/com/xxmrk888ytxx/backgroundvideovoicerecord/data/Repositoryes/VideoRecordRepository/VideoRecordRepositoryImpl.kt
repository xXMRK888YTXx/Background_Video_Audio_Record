package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.VideoFileNameRepository
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
            val newFilePath = newFile
            fileForRecord.renameTo(newFilePath)
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

    override suspend fun removeFile(id: Long) {
        try {
            File(videoDir,id.toString()).delete()

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

    //files

    private val fileForRecord:File
        get() = File(videoDir,"recording")

    private val videoDir : File by lazy {
        ContextWrapper(context).getDir("RecordedVideos", Context.MODE_PRIVATE)
    }

    private val newFile:File
        get() {
            val files = videoDir.listFiles() ?: emptyArray()

            val newFileId = if(files.isEmpty()) 0L
            else files.maxOf { it.fileNameToLong() } + 1

            return File(videoDir,newFileId.toString())
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

        return VideoModel(id,duration,createFileTime)
    }
    //

    //Info file providers
    private suspend fun getCreateFileTime(file: File): Long {
        return file.lastModified()
    }

    private suspend fun getFileDuration(file:File) : Long {
        val mediaMetadataRetriever = MediaMetadataRetriever()

        mediaMetadataRetriever.setDataSource(file.absolutePath)

        return mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
    }
    //

    //Flow control
    private suspend fun loadAllFiles() = withContext(Dispatchers.IO) {
        val files = videoDir.listFiles()?.filterNotNull() ?: return@withContext

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