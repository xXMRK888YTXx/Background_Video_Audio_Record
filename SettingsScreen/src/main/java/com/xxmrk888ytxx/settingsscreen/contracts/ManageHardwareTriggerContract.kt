package com.xxmrk888ytxx.settingsscreen.contracts

import kotlinx.coroutines.flow.Flow

interface ManageHardwareTriggerContract {
    val isProximityTriggerEnabled: Flow<Boolean>
    val isHapticFeedbackEnabled: Flow<Boolean>

    suspend fun setProximityTriggerEnabled(enabled: Boolean)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
}
