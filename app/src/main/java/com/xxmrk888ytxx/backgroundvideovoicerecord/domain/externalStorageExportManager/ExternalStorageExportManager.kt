package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.model.ExportToExternalStorageConfig
import kotlinx.coroutines.flow.Flow

interface ExternalStorageExportManager {

    val exportConfig: Flow<ExportToExternalStorageConfig>

    suspend fun changeState(isEnabled: Boolean): Result<Unit>

    suspend fun updateExportFolderPath(folderUri: String): Result<Unit>

    suspend fun updateScanFolderTimeMills(newTimeInMills: Long): Result<Unit>

    suspend fun updateAutoExportAfterCreateNewRecordState(isEnabled: Boolean): Result<Unit>
}