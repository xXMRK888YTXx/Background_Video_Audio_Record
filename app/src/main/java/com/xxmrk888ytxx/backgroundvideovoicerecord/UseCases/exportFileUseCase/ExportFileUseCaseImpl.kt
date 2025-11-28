package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase.WriteFileToExternalStorageUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository.ExternalStorageRepository
import com.xxmrk888ytxx.coreandroid.toDateString
import com.xxmrk888ytxx.worker.model.FileType
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ExportFileUseCaseImpl @Inject constructor(
    private val context: Context,
    private val writeFileToExternalStorageUseCase: WriteFileToExternalStorageUseCase
) : ExportFileUseCase {
    override suspend fun export(
        externalStorageRepository: ExternalStorageRepository,
        fileForExport: File,
        fileType: ExportFileType,
    ): Result<Unit> = runCatching {
        val subFolderForFile = when (fileType) {
            ExportFileType.VIDEO -> externalStorageRepository.videoFolder
            ExportFileType.AUDIO -> externalStorageRepository.audioFolder
        }

        val externalStorageFile = externalStorageRepository.createFile(
            subFolderForFile,
            fileType.mineType,
            provideNameForExportFile(fileForExport, fileType)
        ).getOrThrow()
        writeFileToExternalStorageUseCase.write(fileForExport, externalStorageFile.uri).getOrThrow()
    }

    private val ExportFileType.mineType: String
        get() = when (this) {
            ExportFileType.VIDEO -> "video/$fileExtension"
            ExportFileType.AUDIO -> "audio/$fileExtension"
        }

    private val ExportFileType.fileExtension: String
        get() = when (this) {
            ExportFileType.VIDEO -> "mp4"
            ExportFileType.AUDIO -> "mp3"
        }

    private fun provideNameForExportFile(fileForExport: File, fileType: ExportFileType): String {
        val lastModificationTime = fileForExport.lastModified()
        val generatedExtraName = UUID.randomUUID().toString()

        return "${lastModificationTime.toDateString(context)}-$generatedExtraName.${fileType.fileExtension}"
    }
}