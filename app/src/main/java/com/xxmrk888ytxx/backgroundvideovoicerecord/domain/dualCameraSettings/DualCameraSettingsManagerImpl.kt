package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.dualCameraSettings

import androidx.datastore.preferences.core.intPreferencesKey
import com.xxmrk888ytxx.coreandroid.models.DualCameraPrimary
import com.xxmrk888ytxx.coreandroid.models.PipPosition
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import javax.inject.Inject

@AppScope
class DualCameraSettingsManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : DualCameraSettingsManager {

    private val primaryKey = intPreferencesKey("dual_camera_primary_key")
    private val pipPositionKey = intPreferencesKey("dual_camera_pip_position_key")

    override val primaryCamera: Flow<DualCameraPrimary> =
        preferencesStorage.getProperty(primaryKey, DualCameraPrimary.BACK.id).map {
            DualCameraPrimary.fromId(it)
        }

    override val pipPosition: Flow<PipPosition> =
        preferencesStorage.getProperty(pipPositionKey, PipPosition.TOP_RIGHT.id).map {
            PipPosition.fromId(it)
        }

    override suspend fun setPrimaryCamera(primary: DualCameraPrimary) {
        preferencesStorage.writeProperty(primaryKey, primary.id)
    }

    override suspend fun setPipPosition(position: PipPosition) {
        preferencesStorage.writeProperty(pipPositionKey, position.id)
    }
}
