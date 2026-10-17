package com.xxmrk888ytxx.settingsscreen.contracts

import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import kotlinx.coroutines.flow.Flow

interface ManageQuickTileContract {
    val quickTileAction: Flow<QuickTileAction>
    suspend fun setQuickTileAction(action: QuickTileAction)

    val quickTileCameraType: Flow<QuickTileCameraType>
    suspend fun setQuickTileCameraType(cameraType: QuickTileCameraType)
}
