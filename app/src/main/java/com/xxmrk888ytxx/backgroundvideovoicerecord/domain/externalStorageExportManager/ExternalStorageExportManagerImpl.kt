package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.externalStorageExportManager.model.ExportToExternalStorageConfig
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExternalStorageExportManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
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
    }

    override suspend fun updateExportFolderPath(folderUri: String): Result<Unit> = doAction {
        preferencesStorage.writeProperty(exportFolderUriPreferencesKey, folderUri)
    }

    override suspend fun updateScanFolderTimeMills(newTimeInMills: Long): Result<Unit> = doAction {
        preferencesStorage.writeProperty(scanFolderTimePreferencesKey, newTimeInMills)
    }

    override suspend fun updateAutoExportAfterCreateNewRecordState(isEnabled: Boolean): Result<Unit> =
        doAction {
            preferencesStorage.writeProperty(
                updateAutoExportAfterCreateNewRecordPreferencesKey,
                isEnabled
            )
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