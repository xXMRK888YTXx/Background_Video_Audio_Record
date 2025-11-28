package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.model.ExportToExternalStorageConfig
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import com.xxmrk888ytxx.worker.model.FileType
import com.xxmrk888ytxx.worker.workManagerController.WorkManagerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ExternalStorageExportManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage,
    private val workManagerController: WorkManagerController
) : ExternalStorageExportManager {

    private val isExportEnabledPreferencesKey = booleanPreferencesKey(EXPORT_ENABLE_PREFERENCES_KEY)
    private val exportFolderUriPreferencesKey =
        stringPreferencesKey(EXPORT_FOLDER_URI_PREFERENCES_LEY)
    private val scanFolderTimePreferencesKey = longPreferencesKey(SCAN_FOLDER_TIME_PREFERENCES_KEY)
    private val updateAutoExportAfterCreateNewRecordPreferencesKey =
        booleanPreferencesKey(UPDATE_AUTO_EXPORT_AFTER_CREATE_NEW_RECORD_PREFERENCES_KEY)

    override val exportConfig: Flow<ExportToExternalStorageConfig> = combine(
        preferencesStorage.getProperty(isExportEnabledPreferencesKey, false),
        preferencesStorage.getPropertyOrNull(exportFolderUriPreferencesKey),
        preferencesStorage.getProperty(scanFolderTimePreferencesKey, DEFAULT_SCAN_FOLDER_TIME),
        preferencesStorage.getProperty(updateAutoExportAfterCreateNewRecordPreferencesKey, false)
    ) { isExportEnabled, exportFolderUriString, scanFolderTime, isAutoExportAfterCreateNewRecordEnabled ->
        ExportToExternalStorageConfig(
            isExportEnabled,
            exportFolderUriString,
            scanFolderTime,
            isAutoExportAfterCreateNewRecordEnabled
        )
    }

    override suspend fun changeState(isEnabled: Boolean): Result<Unit> = doAction {
        if (isEnabled == exportConfig.first().isExportEnabled) return@doAction

        preferencesStorage.writeProperty(isExportEnabledPreferencesKey, isEnabled)

        when(isEnabled) {
            true -> enableAutoExport()
            false -> disableAutoExport()
        }
    }

    override suspend fun updateExportFolderPath(folderUri: String): Result<Unit> = doAction {
        preferencesStorage.writeProperty(exportFolderUriPreferencesKey, folderUri)
    }

    override suspend fun updateScanFolderTimeMills(newTimeInMills: Long): Result<Unit> = doAction {
        preferencesStorage.writeProperty(scanFolderTimePreferencesKey, newTimeInMills)
        enableAutoExport()
    }

    private suspend fun enableAutoExport() {
        val exportConfig = exportConfig.first()
        if (!exportConfig.isExportEnabled) return
        workManagerController.enableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage(exportConfig.scanFolderTimeMills)
    }

    private suspend fun disableAutoExport() {
        workManagerController.disableWorkerForPeriodicallyExportRecordAndVideoToExternalStorage()
    }

    override suspend fun updateAutoExportAfterCreateNewRecordState(isEnabled: Boolean): Result<Unit> =
        doAction {
            preferencesStorage.writeProperty(
                updateAutoExportAfterCreateNewRecordPreferencesKey,
                isEnabled
            )
        }

    override suspend fun exportNewAudioFile(file: File){
        if (!isNeedExportNewFile()) return
        workManagerController.runExportForSingleFile(file, FileType.AUDIO)
    }

    override suspend fun exportNewVideoFile(file: File) {
        if (!isNeedExportNewFile()) return
        workManagerController.runExportForSingleFile(file, FileType.VIDEO)
    }

    private suspend fun isNeedExportNewFile(): Boolean {
        val config = exportConfig.first()
        return config.isExportEnabled && config.exportFolderUriString != null && config.isAutoExportAfterCreateNewRecordEnabled
    }



    private suspend fun doAction(block: suspend () -> Unit): Result<Unit> =
        runCatching { withContext(Dispatchers.IO) { block() } }

    private companion object {

        const val EXPORT_ENABLE_PREFERENCES_KEY = "isExternalStorageExportEnabledPreferencesKey"

        const val EXPORT_FOLDER_URI_PREFERENCES_LEY = "exportFolderUriPreferencesKey"

        const val SCAN_FOLDER_TIME_PREFERENCES_KEY = "scanFolderTimePreferencesKey"

        const val UPDATE_AUTO_EXPORT_AFTER_CREATE_NEW_RECORD_PREFERENCES_KEY =
            "updateAutoExportAfterCreateNewRecordPreferencesKey"
        const val DEFAULT_SCAN_FOLDER_TIME = 7_200_000L
    }
}