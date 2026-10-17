package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.volumeKey

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AppScope
class VolumeKeyActionManagerImpl @Inject constructor(
    private val context: Context,
    private val preferencesStorage: PreferencesStorage
) : VolumeKeyActionManager {

    private val isEnabledKey = booleanPreferencesKey("VOLUME_KEY_TRIGGER_ENABLED_KEY")
    private val actionKey = intPreferencesKey("VOLUME_KEY_ACTION_KEY")
    private val cameraTypeKey = intPreferencesKey("VOLUME_KEY_CAMERA_TYPE_KEY")

    override val isTriggerEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(isEnabledKey, false)

    override suspend fun setTriggerEnabled(enabled: Boolean) {
        preferencesStorage.writeProperty(isEnabledKey, enabled)
    }

    override val currentAction: Flow<VolumeKeyAction> =
        preferencesStorage.getProperty(actionKey, VolumeKeyAction.START_VIDEO_RECORD.id).map {
            VolumeKeyAction.fromId(it)
        }

    override suspend fun setAction(action: VolumeKeyAction) {
        preferencesStorage.writeProperty(actionKey, action.id)
    }

    override val currentCameraType: Flow<QuickTileCameraType> =
        preferencesStorage.getProperty(cameraTypeKey, QuickTileCameraType.AS_IN_SETTINGS.id).map {
            QuickTileCameraType.fromId(it)
        }

    override suspend fun setCameraType(cameraType: QuickTileCameraType) {
        preferencesStorage.writeProperty(cameraTypeKey, cameraType.id)
    }

    override fun isVolumeKeyPermissionGranted(): Boolean {
        return context.checkSelfPermission("android.permission.SET_VOLUME_KEY_LONG_PRESS_LISTENER") ==
                PackageManager.PERMISSION_GRANTED
    }

    override fun isOverlayPermissionGranted(): Boolean {
        return android.provider.Settings.canDrawOverlays(context)
    }

    override fun isAllPermissionsGranted(): Boolean {
        return isVolumeKeyPermissionGranted() && isOverlayPermissionGranted()
    }
}
