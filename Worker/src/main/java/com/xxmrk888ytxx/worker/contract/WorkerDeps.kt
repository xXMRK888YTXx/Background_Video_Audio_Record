package com.xxmrk888ytxx.worker.contract

interface WorkerDeps {
    val notificationInfoProviderContract: NotificationInfoProviderContract

    val singleFileExportWorkerWork: SingleFileExportWorkerWork
}