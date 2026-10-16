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

    override val isVolumeTriggerEnabled: Flow<Boolean> =
        hardwareTriggerManager.isVolumeTriggerEnabled

    override val isProximityTriggerEnabled: Flow<Boolean> =
        hardwareTriggerManager.isProximityTriggerEnabled

    override val isHapticFeedbackEnabled: Flow<Boolean> =
        hardwareTriggerManager.isHapticFeedbackEnabled

    override suspend fun setVolumeTriggerEnabled(enabled: Boolean) {
        hardwareTriggerManager.setVolumeTriggerEnabled(enabled)
    }

    override suspend fun setProximityTriggerEnabled(enabled: Boolean) {
        hardwareTriggerManager.setProximityTriggerEnabled(enabled)
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        hardwareTriggerManager.setHapticFeedbackEnabled(enabled)
    }

    override fun isAccessibilityServiceEnabled(): Boolean {
        return hardwareTriggerManager.isAccessibilityServiceEnabled(context)
    }

    override fun openAccessibilitySettings() {
        hardwareTriggerManager.openAccessibilitySettings(context)
    }
}
