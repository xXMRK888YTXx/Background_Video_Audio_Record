package com.xxmrk888ytxx.worker.workManagerController

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.xxmrk888ytxx.worker.model.FileType
import com.xxmrk888ytxx.worker.worker.AllRecordAutoExportWorker
import com.xxmrk888ytxx.worker.worker.AllRecordAutoExportWorker.Companion.ALL_RECORD_AUTO_EXPORT_NAME
import com.xxmrk888ytxx.worker.worker.SingleFileExportWorker
import com.xxmrk888ytxx.worker.worker.SingleFileExportWorker.Companion.SINGLE_FILE_WORKER_NAME
import java.io.File
import java.util.concurrent.TimeUnit

internal class WorkManagerControllerImpl(
    private val context: Context
) : WorkManagerController {

    private val workManager by lazy { WorkManager.getInstance(context) }

    override fun runExportForSingleFile(
        fileFile: File,
        fileType: FileType
    ): Result<Unit> = runCatching {
        val data = Data.Builder()
            .putString(SingleFileExportWorker.FILE_FILE_KEY,fileFile.absolutePath)
            .putInt(SingleFileExportWorker.FILE_TYPE_KEY,fileType.id)
            .build()

        val worker = OneTimeWorkRequestBuilder<SingleFileExportWorker>()
            .setInputData(data)
            .addTag(SINGLE_FILE_WORKER_NAME)
            .build()

        workManager.enqueue(worker)
    }


    override fun enableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(
        repeatIntervalInMillis: Long
    ): Result<Unit> = runCatching {
        val worker = PeriodicWorkRequestBuilder<AllRecordAutoExportWorker>(repeatIntervalInMillis, TimeUnit.MILLISECONDS)
            .addTag(ALL_RECORD_AUTO_EXPORT_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            ALL_RECORD_AUTO_EXPORT_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, worker
        )
    }

    override fun disableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(): Result<Unit> = runCatching {
        workManager.cancelAllWorkByTag(ALL_RECORD_AUTO_EXPORT_NAME)
    }
}