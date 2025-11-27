package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.ExportManager

import kotlinx.coroutines.flow.Flow

interface ExternalStorageExportManager {

    val isExportEnabled: Flow<Boolean>

    val exportFolderUri: Flow<String?>

    suspend fun changeState(isEnabled: Boolean) : Result<Unit>

    suspend fun updateExportFolderPath(folderUri: String) : Result<Unit>
}