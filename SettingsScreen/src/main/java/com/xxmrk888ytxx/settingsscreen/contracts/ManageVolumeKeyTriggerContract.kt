package com.xxmrk888ytxx.settingsscreen.contracts

import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction
import kotlinx.coroutines.flow.Flow

interface ManageVolumeKeyTriggerContract {
    val isTriggerEnabled: Flow<Boolean>
    val isVolumeKeyPermissionGranted: Boolean
    val isOverlayPermissionGranted: Boolean
    val isAllPermissionsGranted: Boolean
    val volumeKeyAction: Flow<VolumeKeyAction>
    val volumeKeyCameraType: Flow<QuickTileCameraType>

    suspend fun setTriggerEnabled(enabled: Boolean)
    suspend fun setVolumeKeyAction(action: VolumeKeyAction)
    suspend fun setVolumeKeyCameraType(cameraType: QuickTileCameraType)
}
