package com.xxmrk888ytxx.backgroundvideovoicerecord.presentation.volumeKey

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            (context.applicationContext as? App)?.appComponent?.inject(this)

            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val isEnabled = volumeKeyActionManager.isTriggerEnabled.first()
                    val hasPermissions = volumeKeyActionManager.isAllPermissionsGranted()
                    if (isEnabled && hasPermissions) {
                        VolumeKeyTriggerService.start(context)
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
