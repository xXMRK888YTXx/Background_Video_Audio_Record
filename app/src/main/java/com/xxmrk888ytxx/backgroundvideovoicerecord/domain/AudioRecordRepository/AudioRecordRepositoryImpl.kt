package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaMetadataRetriever
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordRepository.models.AudioModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

class AudioRecordRepositoryImpl @Inject constructor(
    private val context:Context
) : AudioRecordRepository {

    private val _fileList = MutableStateFlow(emptyList<AudioModel>())

    override suspend fun addFile(file: File) {
        val buffer = ByteArray(4096)
        var bytesRead: Int
        val newAudioFile = newFile

        withContext(Dispatchers.IO) {
            FileInputStream(file).use { inputStream ->
                FileOutputStream(newAudioFile).use { outputStream ->
                    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }
        }

        loadNewFile(file)
    }

    override suspend fun getFileById(id: Int): File? = withContext(Dispatchers.IO) {
        val file = File(audioDir,id.toString())

        return@withContext if(file.exists()) file else null
    }

    override val fileList: Flow<List<AudioModel>> = _fileList.asStateFlow().onStart {
        loadAllFiles()
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

        _fileList.update { _ -> files.map { it.toAudioModel() }.sortedByDescending { it.created } }
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