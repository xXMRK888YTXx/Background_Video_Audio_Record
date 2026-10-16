package com.xxmrk888ytxx.settingsscreen.contracts

import kotlinx.coroutines.flow.Flow

interface ManageHardwareTriggerContract {
    val isVolumeTriggerEnabled: Flow<Boolean>
    val isProximityTriggerEnabled: Flow<Boolean>
    val isHapticFeedbackEnabled: Flow<Boolean>

    suspend fun setVolumeTriggerEnabled(enabled: Boolean)
    suspend fun setProximityTriggerEnabled(enabled: Boolean)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    fun isAccessibilityServiceEnabled(): Boolean
    fun openAccessibilitySettings()
}
