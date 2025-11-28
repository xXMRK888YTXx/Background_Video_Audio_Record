package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaMetadataRetriever
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.AudioFileNameRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.models.AudioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AudioRecordRepositoryImpl @Inject constructor(
    private val context:Context,
    private val audioFileNameRepository: AudioFileNameRepository
) : AudioRecordRepository {

    private val _fileList = MutableStateFlow(emptyList<AudioModel>())

    override suspend fun addFileFromRecorded() : File {
        if(fileForRecord.exists()) {
            val newFilePath = newFile
            fileForRecord.renameTo(newFilePath)
            loadNewFile(newFilePath)
            return newFilePath
        } else {
            throw IllegalStateException("Folder for audio records is not created")
        }
    }

    override suspend fun getFileById(id: Int): File? = withContext(Dispatchers.IO) {
        val file = File(audioDir,id.toString())

        return@withContext if(file.exists()) file else null
    }

    override suspend fun removeFile(id: Int):Unit = withContext(Dispatchers.IO) {
        try {
            File(audioDir,id.toString()).delete()

            deleteFileFromFlow(id)

            audioFileNameRepository.removeName(id.toLong())
        }catch (_:Exception) {}
    }

    private suspend fun deleteFileFromFlow(id:Int) {
        _fileList.update {
            it.toMutableList()
            .filter { fileId -> fileId.id != id.toLong() }
        }
    }

    override val fileList: Flow<List<AudioModel>> = _fileList
        .asStateFlow()
        .onStart {
            loadAllFiles()
        }
        .combine(audioFileNameRepository.audioNamesMapFlow) { list,nameMap ->
            list.map { it.copy(name = nameMap[it.id]?.name) }
        }

    private val fileForRecord:File
        get() = File(audioDir,"recorded")

    override suspend fun getFileForRecord(): File {
        if(fileForRecord.exists()) {
            addFileFromRecorded()
        }

        return fileForRecord
    }


    private val newFile:File
        get() {
            val files = audioDir.listFiles() ?: emptyArray()

            val newFileId = if(files.isEmpty()) 0L
                else files.maxOf { it.fileNameToLong() } + 1

            return File(audioDir,newFileId.toString())
        }

    private val audioDir : File
        get() = ContextWrapper(context).getDir("RecordedAudios", Context.MODE_PRIVATE)


    private fun File.fileNameToLong() : Long {
        return try {
            nameWithoutExtension.toLong()
        }catch (e:Exception) {
            -1
        }
    }

    private suspend fun loadAllFiles() = withContext(Dispatchers.IO) {
        val files = audioDir.listFiles()?.filterNotNull() ?: return@withContext



        _fileList.update { _ ->
            files
                .map { it.toAudioModel() }
                .filter { it.id != -1L }
                .sortedByDescending { it.created }
        }
    }

    private suspend fun loadNewFile(file: File) = withContext(Dispatchers.IO) {
        _fileList.update { it
            .toMutableList()
            .also { list -> list.add(file.toAudioModel()) }
            .sortedByDescending { model ->  model.created }
            .toList()
        }
    }

    private fun File.toAudioModel() : AudioModel {
        val id = fileNameToLong()
        val duration = getFileDuration(this)
        val createFileTime = getCreateFileTime(this)

        return AudioModel(id,duration,createFileTime)
    }


    private fun getCreateFileTime(file: File): Long {
        return file.lastModified()
    }

    private fun getFileDuration(file:File) : Long {
        val mediaMetadataRetriever = MediaMetadataRetriever()

        mediaMetadataRetriever.setDataSource(file.absolutePath)

        return mediaMetadataRetriever
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
    }


}