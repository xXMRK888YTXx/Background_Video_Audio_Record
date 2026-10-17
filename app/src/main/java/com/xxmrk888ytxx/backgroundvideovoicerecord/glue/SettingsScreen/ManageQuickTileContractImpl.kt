package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.quickTile.QuickTileActionManager
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.settingsscreen.contracts.ManageQuickTileContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageQuickTileContractImpl @Inject constructor(
    private val quickTileActionManager: QuickTileActionManager
) : ManageQuickTileContract {

    override val quickTileAction: Flow<QuickTileAction> = quickTileActionManager.currentAction

    override suspend fun setQuickTileAction(action: QuickTileAction) {
        quickTileActionManager.setAction(action)
    }

    override val quickTileCameraType: Flow<QuickTileCameraType> = quickTileActionManager.currentCameraType

    override suspend fun setQuickTileCameraType(cameraType: QuickTileCameraType) {
        quickTileActionManager.setCameraType(cameraType)
    }
}
