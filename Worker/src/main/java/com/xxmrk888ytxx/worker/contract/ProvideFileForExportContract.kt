package com.xxmrk888ytxx.worker.contract

import java.io.File

interface ProvideFileForExportContract {
    suspend fun provide(filePath: String): Result<File>
}