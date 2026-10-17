package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.volumeKey

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.xxmrk888ytxx.backgroundvideovoicerecord.App
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.volumeKey.VolumeKeyActionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var volumeKeyActionManager: VolumeKeyActionManager

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(LOG_TAG, "Received action: $action")

        val supportedActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            VolumeKeyTriggerService.ACTION_RESTART_VOLUME_KEY_SERVICE,
            VolumeKeyTriggerService.ACTION_WATCHDOG_CHECK
        )

        if (action !in supportedActions) return

        val pendingResult = goAsync()
        val app = context.applicationContext as? App
        app?.appComponent?.inject(this)

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val isEnabled = volumeKeyActionManager.isTriggerEnabled.first()
                val hasPermissions = volumeKeyActionManager.isAllPermissionsGranted()

                if (isEnabled && hasPermissions) {
                    if (!VolumeKeyTriggerService.isRunning) {
                        Log.i(LOG_TAG, "Starting VolumeKeyTriggerService on action: $action")
                        VolumeKeyTriggerService.start(context)
                    }
                    VolumeKeyTriggerService.scheduleWatchdog(context)
                } else {
                    VolumeKeyTriggerService.cancelWatchdog(context)
                }
            } catch (e: Exception) {
                Log.e(LOG_TAG, "Error handling broadcast action: $action", e)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        private const val LOG_TAG = "BootReceiver"
    }
}
