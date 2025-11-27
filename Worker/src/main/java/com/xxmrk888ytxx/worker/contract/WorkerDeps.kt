package com.xxmrk888ytxx.worker.contract

interface WorkerDeps {
    val provideFileForExportContract: ProvideFileForExportContract

    val notificationInfoProviderContract: NotificationInfoProviderContract

    val provideFolderForExportContract: ProvideFolderForExportContract

    val exportFileNameProvideContract: ExportFileNameProvideContract

    val exportFileContract: ExportFileContract
}