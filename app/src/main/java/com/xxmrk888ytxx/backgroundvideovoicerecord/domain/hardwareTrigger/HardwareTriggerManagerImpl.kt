package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.hardwareTrigger

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.xxmrk888ytxx.audiorecordservice.models.RecordAudioState
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.AudioRecordServiceManager.AudioRecordServiceManager
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IsCanStartRecordVideoServiceUseCase.IsCanStartRecordVideoServiceUseCase
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.VideoRecordServiceManager.VideoRecordServiceManager
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import com.xxmrk888ytxx.recordvideoservice.models.RecordVideoState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

class HardwareTriggerManagerImpl @Inject constructor(
    private val context: Context,
    private val preferencesStorage: PreferencesStorage,
    private val videoRecordServiceManager: VideoRecordServiceManager,
    private val audioRecordServiceManager: AudioRecordServiceManager,
    private val isCanStartRecordVideoServiceUseCase: IsCanStartRecordVideoServiceUseCase
) : HardwareTriggerManager {

    private val volumeTriggerKey = booleanPreferencesKey("isVolumeTriggerEnabledKey")
    private val proximityTriggerKey = booleanPreferencesKey("isProximityTriggerEnabledKey")
    private val hapticFeedbackKey = booleanPreferencesKey("isHapticFeedbackEnabledKey")

    override val isVolumeTriggerEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(volumeTriggerKey, false)

    override val isProximityTriggerEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(proximityTriggerKey, false)

    override val isHapticFeedbackEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(hapticFeedbackKey, true)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (isProximityTriggerEnabled.first()) {
                    val intent = Intent(context, ProximityTriggerService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override suspend fun setVolumeTriggerEnabled(enabled: Boolean) {
        preferencesStorage.writeProperty(volumeTriggerKey, enabled)
    }

    override suspend fun setProximityTriggerEnabled(enabled: Boolean) {
        preferencesStorage.writeProperty(proximityTriggerKey, enabled)
        val intent = Intent(context, ProximityTriggerService::class.java)
        if (enabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        } else {
            try {
                context.stopService(intent)
            } catch (_: Exception) {}
        }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        preferencesStorage.writeProperty(hapticFeedbackKey, enabled)
    }

    override fun toggleRecording(context: Context) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val videoState = videoRecordServiceManager.currentRecordState.first()
                val audioState = audioRecordServiceManager.currentRecordState.first()

                val isRecording = videoState !is RecordVideoState.Idle || audioState !is RecordAudioState.Idle
                if (isRecording) {
                    if (videoState !is RecordVideoState.Idle) {
                        videoRecordServiceManager.stopRecord()
                    }
                    if (audioState !is RecordAudioState.Idle) {
                        audioRecordServiceManager.stopRecord()
                    }
                    if (isHapticFeedbackEnabled.first()) {
                        vibratePattern(context, isStarting = false)
                    }
                } else {
                    if (isCanStartRecordVideoServiceUseCase.execute()) {
                        videoRecordServiceManager.startRecord()
                        if (isHapticFeedbackEnabled.first()) {
                            vibratePattern(context, isStarting = true)
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    override fun vibratePattern(context: Context, isStarting: Boolean) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator?.hasVibrator() == true) {
                if (isStarting) {
                    // Two short pulses (120ms, 100ms pause, 120ms)
                    val timings = longArrayOf(0, 120, 100, 120)
                    val amplitudes = intArrayOf(0, 220, 0, 220)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(timings, -1)
                    }
                } else {
                    // One long pulse (350ms)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(350)
                    }
                }
            }
        } catch (_: Exception) {}
    }

    override fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)
        val expectedClassName = HardwareTriggerAccessibilityService::class.java.name
        return enabledServices.any { it.resolveInfo.serviceInfo.name == expectedClassName }
    }

    override fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
