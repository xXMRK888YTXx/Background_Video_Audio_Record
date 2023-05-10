package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager

import androidx.datastore.preferences.core.intPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraConfig
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraRotation
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.CameraType
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.CameraConfigManager.models.MaxQuality
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CameraConfigManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : CameraConfigManager {

    private val cameraTypeKey = intPreferencesKey("cameraTypeKey")

    private val cameraMaxQualityKey = intPreferencesKey("cameraMaxQualityKey")

    private val cameraRotationKey = intPreferencesKey("cameraRotationKey")

    private val cameraTypeFlow = preferencesStorage.getProperty(cameraTypeKey,CameraType.Back.id).map {
        when(it) {
            CameraType.Back.id -> CameraType.Back

            CameraType.Front.id -> CameraType.Front

            else -> error("Not valid camera type")
        }
    }

    private val cameraMaxQualityFlow = preferencesStorage.getProperty(cameraMaxQualityKey,MaxQuality.HD.id).map {
        when(it) {
            MaxQuality.SD.id -> MaxQuality.SD

            MaxQuality.HD.id -> MaxQuality.HD

            MaxQuality.FHD.id -> MaxQuality.FHD

            else -> error("Not valid camera max quality")
        }
    }

    private val cameraRotationFlow =
        preferencesStorage.getProperty(cameraRotationKey,CameraRotation.ROTATION_0.id).map {
            when(it) {
                CameraRotation.ROTATION_0.id -> CameraRotation.ROTATION_0

                CameraRotation.ROTATION_90.id -> CameraRotation.ROTATION_90

                CameraRotation.ROTATION_180.id -> CameraRotation.ROTATION_180

                CameraRotation.ROTATION_270.id -> CameraRotation.ROTATION_270

                else -> error("Not valid camera rotation")
            }
        }

    override val config: Flow<CameraConfig> = combine(
        cameraTypeFlow,cameraMaxQualityFlow,cameraRotationFlow
    ) { cameraType, maxQuality,cameraRotation ->
        CameraConfig(cameraType, maxQuality,cameraRotation)
    }

    override suspend fun setCameraType(cameraType: CameraType) {
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(cameraTypeKey,cameraType.id)
        }
    }

    override suspend fun setMaxQuality(maxQuality: MaxQuality) {
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(cameraMaxQualityKey,maxQuality.id)
        }
    }

    override suspend fun setCameraRotation(cameraRotation: CameraRotation) {
        withContext(Dispatchers.IO) {
            preferencesStorage.writeProperty(cameraRotationKey,cameraRotation.id)
        }
    }
}