package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase.ExportFileType
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase.ExportFileUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase.WriteFileToExternalStorageUseCase
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
                externalStorageRepositoryFactory.createFromDocumentFile(
                    externalStorageFolder,
                    context
                )
            } catch (_: Exception) {
                throw FolderForExportRemovedException()
            }

            exportFileUseCase.export(
                externalManagerRepository, fileForExport,
                when (fileType) {
                    FileType.VIDEO -> ExportFileType.VIDEO
                    FileType.AUDIO -> ExportFileType.AUDIO
                }
            ).getOrThrow()
        }
    }

}