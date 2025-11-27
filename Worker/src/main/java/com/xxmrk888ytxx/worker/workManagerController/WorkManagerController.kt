package com.xxmrk888ytxx.worker.workManagerController

import com.xxmrk888ytxx.worker.model.FileType
import java.io.File

interface WorkManagerController {
    fun runExportForSingleFile(fileFile: File,fileType: FileType): Result<Unit>

    fun enableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(): Result<Unit>

    fun disableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(): Result<Unit>
}