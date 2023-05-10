package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.ExportFileUseCase

import android.net.Uri
import java.io.File

interface ExportFileUseCase {

    suspend fun export(exportFile:File,outputPath:Uri) : Result<Unit>
}