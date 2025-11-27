package com.xxmrk888ytxx.worker.contract

import java.io.File

interface ProvideFolderForExportContract {
    suspend fun provide(fileType: Int): Result<File>
}