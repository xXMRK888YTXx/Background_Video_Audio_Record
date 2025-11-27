package com.xxmrk888ytxx.worker.worker

import android.content.Context
import androidx.work.WorkerParameters
import com.xxmrk888ytxx.coreandroid.toDateString
import com.xxmrk888ytxx.worker.contract.ExportFileContract
import com.xxmrk888ytxx.worker.contract.ExportFileNameProvideContract
import com.xxmrk888ytxx.worker.contract.ProvideFileForExportContract
import com.xxmrk888ytxx.worker.contract.ProvideFolderForExportContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SingleFileExportWorker internal constructor(
    private val context: Context,
    params: WorkerParameters
) : BaseExportWorker(context, params) {

    override val logTag: String = WORKER_NAME

    private val provideFileForExportContract: ProvideFileForExportContract by lazy { workerDeps.provideFileForExportContract }

    private val provideFolderForExport: ProvideFolderForExportContract by lazy { workerDeps.provideFolderForExportContract }

    private val exportFileNameProvideContract: ExportFileNameProvideContract by lazy { workerDeps.exportFileNameProvideContract }

    private val exportFileContract: ExportFileContract by lazy { workerDeps.exportFileContract }



    override suspend fun doWork(): Result = doAction {
        val filePath = inputData.getString(FILE_PATH_KEY) ?: throw IllegalArgumentException("filePath is not provided")
        val fileType = inputData.getInt(FILE_TYPE_KEY, Int.MIN_VALUE)
        if (fileType == Int.MIN_VALUE) throw IllegalArgumentException("fileType is not provided")


        val fileForExport = provideFileForExportContract.provide(filePath).getOrThrow()
        val provideFolderForExport = provideFolderForExport.provide(fileType).getOrThrow()
        val exportFile =
            File(provideFolderForExport, exportFileNameProvideContract.provide(fileForExport))

        exportFileContract.export(fileForExport, exportFile).getOrThrow()
    }

    companion object {
        const val FILE_PATH_KEY: String = "FILE_PATH_KEY"

        const val FILE_TYPE_KEY = "FILE_TYPE_KEY"

        const val WORKER_NAME = "SingleFileExportWorker"
    }
}