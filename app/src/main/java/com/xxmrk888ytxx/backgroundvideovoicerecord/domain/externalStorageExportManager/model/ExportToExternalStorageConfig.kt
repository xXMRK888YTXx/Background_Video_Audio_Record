package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.model


data class ExportToExternalStorageConfig(
    val isExportEnabled: Boolean,
    val exportFolderUriString: String?,
    val scanFolderTimeMills: Long,
    val isAutoExportAfterCreateNewRecordEnabled: Boolean,
)
