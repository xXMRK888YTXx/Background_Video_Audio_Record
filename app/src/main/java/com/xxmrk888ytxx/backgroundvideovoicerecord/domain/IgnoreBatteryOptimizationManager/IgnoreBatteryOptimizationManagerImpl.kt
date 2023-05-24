package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.IgnoreBatteryOptimizationManager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.core.content.getSystemService
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IgnoreBatteryOptimizationManagerImpl @Inject constructor(
    private val context: Context,
    private val preferencesStorage: PreferencesStorage,
) : IgnoreBatteryOptimizationManager {

    private val isNeedShowDialogForDisableBatteryOptimizationKey =
        booleanPreferencesKey("isNeedShowDialogForDisableBatteryOptimizationKey")

    override val isBatteryOptimizationDisabled: Boolean
        get() = context.getSystemService<PowerManager>()
            ?.isIgnoringBatteryOptimizations(context.packageName) ?: false

    override val isNeedShowDialogForDisableBatteryOptimization: Flow<Boolean> =
        preferencesStorage.getProperty(isNeedShowDialogForDisableBatteryOptimizationKey, true)

    override suspend fun hideDisableBatteryDialogForever() {
        preferencesStorage.writeProperty(isNeedShowDialogForDisableBatteryOptimizationKey, false)
    }

    @SuppressLint("BatteryLife")
    override fun openBatterySettings() {
        try {
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(
                LOG_TAG, "open intent with action ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS " +
                        "throw exception ${e.stackTraceToString()}"
            )
        }
    }

    companion object {
        private const val LOG_TAG = "IgnoreBatteryOptimizationManagerImpl"
    }
}