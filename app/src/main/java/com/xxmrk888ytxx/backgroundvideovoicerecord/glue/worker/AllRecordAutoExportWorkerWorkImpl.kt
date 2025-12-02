package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase.ExportFileType
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioRecordRepository.AudioRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoRecordRepository.VideoRecordRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.ExternalStorageExportManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository.ExternalStorageRepository
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository.ExternalStorageRepositoryFactory
import com.xxmrk888ytxx.worker.contract.AllRecordAutoExportWorkerWork
import com.xxmrk888ytxx.worker.exception.FolderForExportRemovedException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import javax.inject.Inject

private typealias MD5Hash = String

class AllRecordAutoExportWorkerWorkImpl @Inject constructor(
    private val audioRecordRepository: AudioRecordRepository,
    private val videoRecordRepository: VideoRecordRepository,
    private val exportFileUseCase: ExportFileUseCase,
    private val context: Context,
    private val externalStorageExportManager: ExternalStorageExportManager,
    private val externalStorageRepositoryFactory: ExternalStorageRepositoryFactory,
) : AllRecordAutoExportWorkerWork {

    override suspend fun doWork(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val externalStorageRepository = createExternalStorageRepository()
            exportAudio(externalStorageRepository)
            exportVideo(externalStorageRepository)
        }
    }

    private suspend fun exportVideo(externalStorageRepository: ExternalStorageRepository) {
        val videoRecordFilesHashInInternalStorage = videoRecordRepository.fileList.first()
            .mapNotNull { videoRecordRepository.getFileById(it.id)?.toMD5HashAndFilePair() }
            .toMap()
        val videoRecordFilesHashInExternalStorage =
            externalStorageRepository.videoFolder.listFiles()
                .mapNotNull { it.toMD5Hash(externalStorageRepository) }.toSet()

        baseExport(
            externalStorageRepository,
            videoRecordFilesHashInInternalStorage, videoRecordFilesHashInExternalStorage,
            ExportFileType.VIDEO
        )
    }

    private suspend fun exportAudio(externalStorageRepository: ExternalStorageRepository) {
        val audioRecordFilesHashInInternalStorage = audioRecordRepository.fileList.first()
            .mapNotNull { audioRecordRepository.getFileById(it.id)?.toMD5HashAndFilePair() }
            .toMap()
        val audioRecordFilesHashInExternalStorage =
            externalStorageRepository.audioFolder.listFiles()
                .mapNotNull { it.toMD5Hash(externalStorageRepository) }.toSet()

        baseExport(
            externalStorageRepository,
            audioRecordFilesHashInInternalStorage,
            audioRecordFilesHashInExternalStorage,
            ExportFileType.AUDIO
        )
    }

    private suspend fun baseExport(
        externalStorageRepository: ExternalStorageRepository,
        internalStorageFiles: Map<MD5Hash, File>,
        externalStorageHash: Set<MD5Hash>,
        exportFileType: ExportFileType,
    ) {
        val audioFilesForExport = mutableListOf<File>()
        internalStorageFiles.forEach {
            if (!externalStorageHash.contains(it.key)) {
                audioFilesForExport.add(it.value)
            }
        }
        audioFilesForExport.forEach {
            exportFileUseCase.export(externalStorageRepository, it, exportFileType)
        }
    }

    private suspend fun File.toMD5HashAndFilePair(): Pair<MD5Hash, File>? = try {
        toMD5Hash() to this
    } catch (_: Exception) {
        null
    }

    private suspend fun File.toMD5Hash(): MD5Hash {
        val md5Digest = MessageDigest.getInstance("MD5")
        val fileBytes = inputStream().buffered().use { it.readFileForGetHash() }
        return md5Digest.digest(fileBytes).toHexString()
    }

    private suspend fun DocumentFile.toMD5Hash(externalStorageRepository: ExternalStorageRepository): MD5Hash {
        val md5Digest = MessageDigest.getInstance("MD5")
        val fileBytes = externalStorageRepository.openInputStream(this).getOrThrow().buffered()
            .use { it.readFileForGetHash() }
        return md5Digest.digest(fileBytes).toHexString()
    }

    private fun InputStream.readFileForGetHash(): ByteArray {
        val byteArraySize = 1000
        val out = ByteArray(byteArraySize)
        read(out,0,byteArraySize)
        return out
    }


    suspend fun createExternalStorageRepository(): ExternalStorageRepository {
        val externalStorageFolderUri =
            externalStorageExportManager.exportConfig.first().exportFolderUriString?.toUri()
                ?: throw FolderForExportRemovedException("External storage Uri is not provided")
        val externalStorageFolder =
            DocumentFile.fromTreeUri(context, externalStorageFolderUri)!!
        return try {
            externalStorageRepositoryFactory.createFromDocumentFile(externalStorageFolder, context)
        } catch (e: Exception) {
            throw FolderForExportRemovedException()
        }
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }
}