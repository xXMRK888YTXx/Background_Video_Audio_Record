package com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model

internal data class ScreenState(
    val isExportEnabled: Boolean = false,
    val isExportFolderSelected: Boolean = false,
    val scanFolderTime: ScanFolderTime = ScanFolderTime.ThreeHour,
    val isAutoExportAfterCreateNewRecordEnabled: Boolean = true,
    val isDropDownVisible: Boolean = false,
)
