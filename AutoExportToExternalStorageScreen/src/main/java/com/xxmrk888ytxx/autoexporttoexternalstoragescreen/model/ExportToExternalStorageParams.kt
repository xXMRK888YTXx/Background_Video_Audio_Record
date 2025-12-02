package com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model

data class ExportToExternalStorageParams(
    val isExportEnabled: Boolean,
    val isExportFolderSelected: Boolean,
    val scanFolderTime: ScanFolderTime,
    val isAutoExportAfterCreateNewRecordEnabled: Boolean,
)
