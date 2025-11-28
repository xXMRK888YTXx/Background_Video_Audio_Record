package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.ExportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.ExternalStorageExportManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository.ExternalStorageRepositoryFactory
import com.xxmrk888ytxx.coreandroid.toDateString
import com.xxmrk888ytxx.worker.contract.SingleFileExportWorkerWork
import com.xxmrk888ytxx.worker.exception.FolderForExportRemovedException
import com.xxmrk888ytxx.worker.model.FileType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class SingleFileExportWorkerWorkImpl @Inject constructor(
    private val context: Context,
    private val externalStorageExportManager: ExternalStorageExportManager,
    private val externalStorageRepositoryFactory: ExternalStorageRepositoryFactory,
    private val exportFileUseCase: ExportFileUseCase
) : SingleFileExportWorkerWork {

    override suspend fun doWork(
        filePath: String,
        fileType: FileType
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val fileForExport = File(filePath)
            val externalStorageFolderUri =
                externalStorageExportManager.exportConfig.first().exportFolderUriString?.toUri()
                    ?: throw FolderForExportRemovedException("External storage Uri is not provided")
            val externalStorageFolder =
                DocumentFile.fromTreeUri(context, externalStorageFolderUri)!!
            val externalManagerRepository = try {
                externalStorageRepositoryFactory.createFromDocumentFile(externalStorageFolder)
            }catch (e: Exception) {
                throw FolderForExportRemovedException()
            }

            val subFolderForFile = when (fileType) {
                FileType.VIDEO -> externalManagerRepository.videoFolder
                FileType.AUDIO -> externalManagerRepository.audioFolder
            }
            val externalStorageFile = externalManagerRepository.createFile(
                subFolderForFile,
                fileType.mineType,
                provideNameForExportFile(fileForExport,fileType)
            ).getOrThrow()
            exportFileUseCase.export(fileForExport, externalStorageFile.uri).getOrThrow()
        }
    }

    private val FileType.mineType: String
        get() = when (this) {
            FileType.VIDEO -> "video/$fileExtension"
            FileType.AUDIO -> "audio/$fileExtension"
        }

    private val FileType.fileExtension: String
        get() = when (this) {
            FileType.VIDEO -> "mp4"
            FileType.AUDIO -> "mp3"
        }

    private fun provideNameForExportFile(fileForExport: File,fileType: FileType): String {
        val lastModificationTime = fileForExport.lastModified()
        val generatedExtraName = UUID.randomUUID().toString()

        return "${lastModificationTime.toDateString(context)}-$generatedExtraName.${fileType.fileExtension}"
    }
}