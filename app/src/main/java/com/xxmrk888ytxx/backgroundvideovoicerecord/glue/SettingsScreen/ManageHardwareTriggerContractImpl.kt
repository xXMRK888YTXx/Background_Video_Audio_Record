package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.hardwareTrigger.HardwareTriggerManager
import com.xxmrk888ytxx.settingsscreen.contracts.ManageHardwareTriggerContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageHardwareTriggerContractImpl @Inject constructor(
    private val context: Context,
    private val hardwareTriggerManager: HardwareTriggerManager
) : ManageHardwareTriggerContract {

    override val isProximityTriggerEnabled: Flow<Boolean> =
        hardwareTriggerManager.isProximityTriggerEnabled

    override val isHapticFeedbackEnabled: Flow<Boolean> =
        hardwareTriggerManager.isHapticFeedbackEnabled

    override suspend fun setProximityTriggerEnabled(enabled: Boolean) {
        hardwareTriggerManager.setProximityTriggerEnabled(enabled)
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        hardwareTriggerManager.setHapticFeedbackEnabled(enabled)
    }
}
