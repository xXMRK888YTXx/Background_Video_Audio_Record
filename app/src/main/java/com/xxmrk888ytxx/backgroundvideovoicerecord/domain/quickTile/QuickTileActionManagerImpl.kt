package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.quickTile

import androidx.datastore.preferences.core.intPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.coreandroid.models.QuickTileAction
import com.xxmrk888ytxx.coreandroid.models.QuickTileCameraType
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AppScope
class QuickTileActionManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : QuickTileActionManager {

    private val actionKey = intPreferencesKey("QUICK_TILE_ACTION_KEY")
    private val cameraTypeKey = intPreferencesKey("QUICK_TILE_CAMERA_TYPE_KEY")

    override val currentAction: Flow<QuickTileAction> =
        preferencesStorage.getProperty(actionKey, QuickTileAction.OPEN_APP.id).map {
            QuickTileAction.fromId(it)
        }

    override suspend fun setAction(action: QuickTileAction) {
        preferencesStorage.writeProperty(actionKey, action.id)
    }

    override val currentCameraType: Flow<QuickTileCameraType> =
        preferencesStorage.getProperty(cameraTypeKey, QuickTileCameraType.AS_IN_SETTINGS.id).map {
            QuickTileCameraType.fromId(it)
        }

    override suspend fun setCameraType(cameraType: QuickTileCameraType) {
        preferencesStorage.writeProperty(cameraTypeKey, cameraType.id)
    }
}
