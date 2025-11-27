package com.xxmrk888ytxx.worker.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication
import com.xxmrk888ytxx.worker.contract.NotificationInfoProviderContract
import com.xxmrk888ytxx.worker.contract.WorkerDeps
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseExportWorker internal constructor(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    abstract val logTag: String

    protected val workerDeps: WorkerDeps by lazy { context.getDepsByApplication() }

    private val notificationInfoProviderContract: NotificationInfoProviderContract by lazy { workerDeps.notificationInfoProviderContract }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            notificationInfoProviderContract.notificationId,
            notificationInfoProviderContract.foregroundServiceNotification
        )
    }

    protected suspend fun doAction(block: suspend () -> Unit) : Result {
        return withContext(Dispatchers.IO) {
            val result = runCatching { block() }.onFailure { Log.e(logTag,it.stackTraceToString()) }
            return@withContext when(result.isSuccess) {
                true -> Result.success()
                false -> Result.failure()
            }
        }
    }
}