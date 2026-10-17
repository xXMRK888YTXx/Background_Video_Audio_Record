package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.quickTile

import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import kotlinx.coroutines.flow.Flow

interface QuickTileActionManager {
    val currentAction: Flow<QuickTileAction>
    suspend fun setAction(action: QuickTileAction)

    val currentCameraType: Flow<QuickTileCameraType>
    suspend fun setCameraType(cameraType: QuickTileCameraType)
}
