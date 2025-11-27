package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.AutoExportToExternalStorageScreen

import android.net.Uri
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.contract.ManageExportToExternalStorageSettingContract
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model.ExportToExternalStorageParams
import com.xxmrk888ytxx.autoexporttoexternalstoragescreen.model.ScanFolderTime
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.ExternalStorageExportManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.model.ExportToExternalStorageConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManageExportToExternalStorageSettingContractImpl @Inject constructor(
    private val externalStorageExportManager: ExternalStorageExportManager
) : ManageExportToExternalStorageSettingContract {

    private val millsTimeToScanFolderTime by lazy {
        mapOf(
            3_600_000L to ScanFolderTime.OneHour,
            7_200_000L to ScanFolderTime.ThreeHour,
            21_600_000L to ScanFolderTime.SixHour,
            43_200_000L to ScanFolderTime.TwelveHour,
            86_400_000L to ScanFolderTime.OneDay
        )
    }

    private val scanFolderTimeToMillsTime by lazy {
        millsTimeToScanFolderTime.map { it.value to it.key }.toMap()
    }

    override val externalStorageExportParams: Flow<ExportToExternalStorageParams> =
        externalStorageExportManager.exportConfig.map { exportConfig ->
            exportConfig.toExportToExternalStorageParams()
        }

    override suspend fun setupExportFolderUri(folderUri: Uri): Result<Unit> {
        val uriString = runCatching { folderUri.toString() }

        return when (uriString.isSuccess) {
            true -> externalStorageExportManager.updateExportFolderPath(uriString.getOrThrow())
            false -> uriString.map { Unit }
        }
    }

    override suspend fun changeExportState(isEnabled: Boolean): Result<Unit> =
        externalStorageExportManager.changeState(isEnabled)

    override suspend fun changeScanFolderTime(scanFolderTime: ScanFolderTime): Result<Unit> =
        externalStorageExportManager.updateScanFolderTimeMills(scanFolderTime.toMills())

    override suspend fun changeAutoExportAfterCreateNewRecordState(isEnabled: Boolean): Result<Unit> =
        externalStorageExportManager.updateAutoExportAfterCreateNewRecordState(isEnabled)


    private fun ExportToExternalStorageConfig.toExportToExternalStorageParams(): ExportToExternalStorageParams =
        ExportToExternalStorageParams(
            isExportEnabled,
            exportFolderUriString != null,
            scanFolderTimeMills.toScanFolderTime(),
            isAutoExportAfterCreateNewRecordEnabled
        )

    private fun Long.toScanFolderTime(): ScanFolderTime =
        millsTimeToScanFolderTime[this] ?: throw IllegalArgumentException("Invalid time")

    private fun ScanFolderTime.toMills(): Long =
        scanFolderTimeToMillsTime[this] ?: throw IllegalArgumentException("Invalid ScanFolderTime")


}