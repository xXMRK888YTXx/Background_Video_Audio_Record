package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.ExportManager

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExternalStorageExportManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : ExternalStorageExportManager {

    private val isExportEnabledPreferencesKey = booleanPreferencesKey(EXPORT_ENABLE_PREFERENCES_KEY)
    private val exportFolderUriPreferencesKey =
        stringPreferencesKey(EXPORT_FOLDER_URI_PREFERENCES_LEY)

    override val isExportEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(isExportEnabledPreferencesKey, false)
    override val exportFolderUri: Flow<String?> =
        preferencesStorage.getPropertyOrNull(exportFolderUriPreferencesKey)

    override suspend fun changeState(isEnabled: Boolean): Result<Unit> = runCatching {
        return@runCatching withContext(Dispatchers.IO) {
            if (isEnabled == isExportEnabled.first()) return@withContext

            preferencesStorage.writeProperty(isExportEnabledPreferencesKey, isEnabled)
        }
    }

    override suspend fun updateExportFolderPath(folderUri: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(
                exportFolderUriPreferencesKey,
                folderUri
            )
        }
    }

    private companion object {
        const val EXPORT_ENABLE_PREFERENCES_KEY = "isExternalStorageExportEnabledPreferencesKey"
        const val EXPORT_FOLDER_URI_PREFERENCES_LEY = "exportFolderUriPreferencesKey"
    }
}