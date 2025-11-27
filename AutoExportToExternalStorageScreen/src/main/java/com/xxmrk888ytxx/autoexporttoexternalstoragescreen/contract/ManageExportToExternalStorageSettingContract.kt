package com.xxmrk888ytxx.autoexporttoexternalstoragescreen.contract

import android.net.Uri
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model.ExportToExternalStorageParams
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model.ScanFolderTime
import kotlinx.coroutines.flow.Flow

interface ManageExportToExternalStorageSettingContract {

    val externalStorageExportParams: Flow<ExportToExternalStorageParams>

    suspend fun setupExportFolderUri(folderUri: Uri): Result<Unit>

    suspend fun changeExportState(isEnabled: Boolean): Result<Unit>

    suspend fun changeScanFolderTime(scanFolderTime: ScanFolderTime): Result<Unit>

    suspend fun changeAutoExportAfterCreateNewRecordState(isEnabled: Boolean): Result<Unit>
}