package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AutoExportToExternalStorageScreen

import android.net.Uri
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.contract.ManageExportToExternalStorageSettingContract
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.ExportManager.ExternalStorageExportManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageExportToExternalStorageSettingContractImpl @Inject constructor(
    private val externalStorageExportManager: ExternalStorageExportManager
) : ManageExportToExternalStorageSettingContract {
    override val isExportEnabled: Flow<Boolean> = externalStorageExportManager.isExportEnabled

    override val isExportFolderSelected: Flow<Boolean> =
        externalStorageExportManager.exportFolderUri.map { it != null }

    override suspend fun setupExportFolderUri(folderUri: Uri): Result<Unit> {
        val uriString = runCatching { folderUri.toString() }



        return when (uriString.isSuccess) {
            true -> externalStorageExportManager.updateExportFolderPath(uriString.getOrThrow())
            false -> uriString.map { Unit }
        }
    }

    override suspend fun changeExportState(isEnabled: Boolean): Result<Unit> =
        externalStorageExportManager.changeState(isEnabled)
}