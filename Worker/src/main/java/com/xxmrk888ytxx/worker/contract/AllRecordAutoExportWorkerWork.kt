package com.xxmrk888ytxx.worker.contract

interface AllRecordAutoExportWorkerWork {
    suspend fun doWork(): Result<Unit>
}