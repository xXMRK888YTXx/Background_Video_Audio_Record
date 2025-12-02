package com.xxmrk888ytxx.backgroundvideovoicerecord.UseCases.writeFileToExternalStorageUseCase

import android.net.Uri
import java.io.File

interface WriteFileToExternalStorageUseCase {

    suspend fun write(exportFile:File, outputPath:Uri) : Result<Unit>
}