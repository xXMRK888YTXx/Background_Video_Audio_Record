package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager

import androidx.datastore.preferences.core.intPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CameraConfigManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : CameraConfigManager {

    private val cameraTypeKey = intPreferencesKey("cameraTypeKey")

    private val cameraTypeFlow by lazy {
        preferencesStorage.getProperty(cameraTypeKey,CameraType.Back.id).map {
            when(it) {
                CameraType.Back.id -> CameraType.Back

                CameraType.Front.id -> CameraType.Front

                else -> error("Not valid camera type")
            }
        }
    }

    override val config: Flow<CameraConfig> = cameraTypeFlow.map {
        CameraConfig(it)
    }

    override suspend fun setCameraType(cameraType: CameraType) {
        preferencesStorage.writeProperty(cameraTypeKey,cameraType.id)
    }
}