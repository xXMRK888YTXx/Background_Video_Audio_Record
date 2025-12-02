package com.xxmrk888ytxx.worker.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.xxmrk888ytxx.worker.contract.SingleFileExportWorkerWork
import com.xxmrk888ytxx.worker.model.FileType

class SingleFileExportWorker internal constructor(
    context: Context,
    params: WorkerParameters
) : BaseExportWorker(context, params) {

    override val logTag: String = SINGLE_FILE_WORKER_NAME

    private val singleFileExportWorkerWork: SingleFileExportWorkerWork by lazy { workerDeps.singleFileExportWorkerWork }



    override suspend fun doWork(): Result = doAction {
        val filePath = inputData.getString(FILE_FILE_KEY) ?: throw IllegalArgumentException("filePath is not provided")
        val fileTypeId = inputData.getInt(FILE_TYPE_KEY, Int.MIN_VALUE)
        if (fileTypeId == Int.MIN_VALUE) throw IllegalArgumentException("fileType is not provided")

        singleFileExportWorkerWork.doWork(filePath, FileType.fromId(fileTypeId)).getOrThrow()
    }

    companion object {
        const val FILE_FILE_KEY: String = "FILE_PATH_KEY"

        const val FILE_TYPE_KEY = "FILE_TYPE_KEY"

        const val SINGLE_FILE_WORKER_NAME = "SingleFileExportWorker"
    }
}