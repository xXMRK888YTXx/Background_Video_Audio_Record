package com.xxmrk888ytxx.worker.contract

import java.io.File

interface ExportFileNameProvideContract {
    suspend fun provide(file: File) : String
}