package com.xxmrk888ytxx.worker.contract

import java.io.File

interface ExportFileContract {
    suspend fun export(from: File, to: File): Result<Unit>
}