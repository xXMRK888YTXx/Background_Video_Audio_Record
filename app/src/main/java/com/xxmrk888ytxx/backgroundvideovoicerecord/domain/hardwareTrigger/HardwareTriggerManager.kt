package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.hardwareTrigger

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface HardwareTriggerManager {
    val isVolumeTriggerEnabled: Flow<Boolean>
    val isProximityTriggerEnabled: Flow<Boolean>
    val isHapticFeedbackEnabled: Flow<Boolean>

    suspend fun setVolumeTriggerEnabled(enabled: Boolean)
    suspend fun setProximityTriggerEnabled(enabled: Boolean)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)

    fun toggleRecording(context: Context)
    fun vibratePattern(context: Context, isStarting: Boolean)
    fun isAccessibilityServiceEnabled(context: Context): Boolean
    fun openAccessibilitySettings(context: Context)
}
