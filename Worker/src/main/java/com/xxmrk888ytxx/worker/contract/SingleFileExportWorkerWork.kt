package com.xxmrk888ytxx.worker.contract

import com.xxmrk888ytxx.worker.model.FileType

interface SingleFileExportWorkerWork {
    suspend fun doWork(filePath: String, fileType: FileType): Result<Unit>
}