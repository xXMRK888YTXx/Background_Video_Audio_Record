package com.xxmrk888ytxx.worker.worker

import android.content.Context
import androidx.work.WorkerParameters

class AllRecordAutoExportWorker(context: Context, workerParameters: WorkerParameters) :
    BaseExportWorker(context, workerParameters) {
    override val logTag: String = ALL_RECORD_AUTO_EXPORT_NAME

    override suspend fun doWork(): Result = doAction { workerDeps.allRecordAutoExportWorkerWork.doWork() }

    companion object {
        const val ALL_RECORD_AUTO_EXPORT_NAME = "AllRecordAutoExportWorker"
    }
}