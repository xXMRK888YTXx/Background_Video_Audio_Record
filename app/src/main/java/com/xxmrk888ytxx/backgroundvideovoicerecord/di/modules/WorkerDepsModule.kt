package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker.AllRecordAutoExportWorkerWorkImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker.NotificationInfoProviderContractImpl
import com.xxmrk888ytxx.backgroundvideovoicerecord.glue.worker.SingleFileExportWorkerWorkImpl
import com.xxmrk888ytxx.worker.contract.AllRecordAutoExportWorkerWork
import com.xxmrk888ytxx.worker.contract.NotificationInfoProviderContract
import com.xxmrk888ytxx.worker.contract.SingleFileExportWorkerWork
import com.xxmrk888ytxx.worker.workManagerController.WorkManagerController
import com.xxmrk888ytxx.worker.workManagerController.WorkManagerControllerFactory
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface WorkerDepsModule {

    @Binds
    fun bindsNotificationInfoProviderContract(
        notificationInfoProviderContractImpl: NotificationInfoProviderContractImpl
    ): NotificationInfoProviderContract

    @Binds
    fun bindsSingleFileExportWorkerWork(
        singleFileExportWorkerWork: SingleFileExportWorkerWorkImpl
    ): SingleFileExportWorkerWork

    @Binds
    fun bindsAllRecordAutoExportWorkerWork(
        allRecordAutoExportWorkerWorkImpl: AllRecordAutoExportWorkerWorkImpl
    ): AllRecordAutoExportWorkerWork

    companion object {
        @Provides
        fun providesWorkManagerController(context: Context): WorkManagerController =
            WorkManagerControllerFactory.create(context)
    }
}