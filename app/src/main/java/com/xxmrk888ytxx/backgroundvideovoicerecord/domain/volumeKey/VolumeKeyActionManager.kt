package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.volumeKey

import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.coreandroid.models.VolumeKeyAction
import kotlinx.coroutines.flow.Flow

interface VolumeKeyActionManager {
    val isTriggerEnabled: Flow<Boolean>
    suspend fun setTriggerEnabled(enabled: Boolean)

    val currentAction: Flow<VolumeKeyAction>
    suspend fun setAction(action: VolumeKeyAction)

    val currentCameraType: Flow<QuickTileCameraType>
    suspend fun setCameraType(cameraType: QuickTileCameraType)

    fun isVolumeKeyPermissionGranted(): Boolean
    fun isOverlayPermissionGranted(): Boolean
    fun isAllPermissionsGranted(): Boolean
}
