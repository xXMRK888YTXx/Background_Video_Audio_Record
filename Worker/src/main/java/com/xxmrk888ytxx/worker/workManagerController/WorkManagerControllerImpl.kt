package com.xxmrk888ytxx.worker.workManagerController

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xxmrk888ytxx.worker.model.FileType
import com.xxmrk888ytxx.worker.worker.SingleFileExportWorker
import com.xxmrk888ytxx.worker.worker.SingleFileExportWorker.Companion.WORKER_NAME
import java.io.File

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
            .build()

        workManager.enqueueUniqueWork(WORKER_NAME, ExistingWorkPolicy.APPEND,worker)
    }


    override fun enableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun disableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(): Result<Unit> {
        TODO("Not yet implemented")
    }
}