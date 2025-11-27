package com.xxmrk888ytxx.autoexporttoexternalstoragescreen.contract

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface ManageExportToExternalStorageSettingContract {

    val isExportEnabled: Flow<Boolean>

    val isExportFolderSelected: Flow<Boolean>

    suspend fun setupExportFolderUri(folderUri: Uri): Result<Unit>

    suspend fun changeExportState(isEnabled: Boolean): Result<Unit>

}