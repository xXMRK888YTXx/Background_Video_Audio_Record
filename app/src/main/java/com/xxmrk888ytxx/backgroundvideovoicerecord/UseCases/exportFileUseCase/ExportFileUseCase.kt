package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.exportFileUseCase

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageRepository.ExternalStorageRepository
import java.io.File

interface ExportFileUseCase {
    suspend fun export(
        externalStorageRepository: ExternalStorageRepository,
        fileForExport: File,
        fileType: ExportFileType
    ): Result<Unit>
}